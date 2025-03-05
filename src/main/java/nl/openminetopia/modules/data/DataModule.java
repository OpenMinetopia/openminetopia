package nl.openminetopia.modules.data;

import lombok.Getter;
import com.jazzkuh.modulemanager.spigot.SpigotModule;
import com.jazzkuh.modulemanager.spigot.SpigotModuleManager;
import nl.openminetopia.OpenMinetopia;
import org.jetbrains.annotations.NotNull;
import nl.openminetopia.configuration.DefaultConfiguration;
import nl.openminetopia.modules.data.adapters.DatabaseAdapter;
import nl.openminetopia.modules.data.adapters.utils.AdapterUtil;
import nl.openminetopia.modules.data.types.DatabaseType;

@Getter
public class DataModule extends SpigotModule<@NotNull OpenMinetopia> {

    private DatabaseAdapter adapter;

    public DataModule(SpigotModuleManager<@NotNull OpenMinetopia> moduleManager) {
        super(moduleManager);
    }

    @Override
    public void onEnable() {
        DefaultConfiguration configuration = OpenMinetopia.getDefaultConfiguration();
        DatabaseType type = configuration.getDatabaseType();

        adapter = AdapterUtil.getAdapter(type);
        adapter.connect();
    }
}
