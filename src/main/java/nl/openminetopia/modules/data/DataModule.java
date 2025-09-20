package nl.openminetopia.modules.data;

import com.jazzkuh.modulemanager.spigot.SpigotModule;
import com.jazzkuh.modulemanager.spigot.SpigotModuleManager;
import lombok.Getter;
import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.configuration.DefaultConfiguration;
import nl.openminetopia.modules.data.adapters.DatabaseAdapter;
import nl.openminetopia.modules.data.adapters.utils.AdapterUtil;
import nl.openminetopia.modules.data.types.DatabaseType;
import nl.openminetopia.utils.FeatureUtils;
import org.jetbrains.annotations.NotNull;

@Getter
public class DataModule extends SpigotModule<@NotNull OpenMinetopia> {

    private DatabaseAdapter adapter;

    public DataModule(SpigotModuleManager<@NotNull OpenMinetopia> moduleManager) {
        super(moduleManager);
    }

    @Override
    public void onEnable() {
        // Check if data feature is enabled
        if (FeatureUtils.isFeatureDisabled("data")) {
            getLogger().info("Data feature is disabled in config.yml");
            return;
        }

        DefaultConfiguration configuration = OpenMinetopia.getDefaultConfiguration();
        DatabaseType type = configuration.getDatabaseType();

        adapter = AdapterUtil.getAdapter(type);
        adapter.connect();
    }
}
