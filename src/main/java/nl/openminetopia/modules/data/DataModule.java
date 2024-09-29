package nl.openminetopia.modules.data;

import com.craftmend.storm.Storm;
import com.craftmend.storm.api.StormModel;
import lombok.Getter;
import lombok.SneakyThrows;
import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.configuration.DefaultConfiguration;
import nl.openminetopia.modules.Module;
import nl.openminetopia.modules.data.adapters.DatabaseAdapter;
import nl.openminetopia.modules.data.adapters.utils.AdapterUtil;
import nl.openminetopia.modules.data.storm.StormDatabase;
import nl.openminetopia.modules.data.storm.adapters.AccountPermissionAdapter;
import nl.openminetopia.modules.data.storm.adapters.AccountTypeAdapter;
import nl.openminetopia.modules.data.storm.models.*;
import nl.openminetopia.modules.data.type.DatabaseType;

@Getter
public class DataModule extends Module {

    private DatabaseAdapter adapter;

    @Override
    public void enable() {
        // TODO: Get this value from the config
        DefaultConfiguration configuration = OpenMinetopia.getDefaultConfiguration();
        DatabaseType type = configuration.getDatabaseType();

        adapter = AdapterUtil.getAdapter(type);
        adapter.connect();

        Storm storm = StormDatabase.getInstance().getStorm();

        if (type != DatabaseType.MONGO) {
            try {
                TypeRegistry.registerAdapter(AccountType.class, new AccountTypeAdapter());
                TypeRegistry.registerAdapter(AccountPermission.class, new AccountPermissionAdapter());

                storm.registerModel(new PlayerModel());
                storm.registerModel(new PrefixesModel());
                storm.registerModel(new ColorsModel());
                storm.registerModel(new FitnessModel());
                storm.registerModel(new FitnessBoosterModel());
                storm.registerModel(new WorldModel());
                storm.registerModel(new CityModel());
                storm.runMigrations();
            } catch (Exception e) {
                OpenMinetopia.getInstance().getLogger().severe("Failed to connect to " + type.name() + " database: " + e.getMessage());
                OpenMinetopia.getInstance().getLogger().severe("Disabling the plugin...");
                OpenMinetopia.getInstance().getServer().getPluginManager().disablePlugin(OpenMinetopia.getInstance());
            }
        }
    }

    @Override
    public void disable() {
        if (adapter != null) {
            adapter.disconnect();
        }
    }

    @SneakyThrows
    private void registerStormModel(StormModel model) {
        Storm storm = StormDatabase.getInstance().getStorm();
        storm.registerModel(model);
        storm.runMigrations();
    }
}
