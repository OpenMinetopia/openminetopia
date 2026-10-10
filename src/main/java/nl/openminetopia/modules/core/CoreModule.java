package nl.openminetopia.modules.core;

import nl.openminetopia.utils.modules.ExtendedSpigotModule;
import com.jazzkuh.modulemanager.spigot.SpigotModuleManager;
import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.modules.core.commands.OpenMinetopiaCommand;
import nl.openminetopia.modules.core.commands.PlayerDataCommand;
import nl.openminetopia.modules.core.playerdata.PlayerDataCategory;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CoreModule extends ExtendedSpigotModule {
    public CoreModule(SpigotModuleManager<@NotNull OpenMinetopia> moduleManager) {
        super(moduleManager);
    }

    @Override
    public void onEnable() {
        registerComponent(new OpenMinetopiaCommand());
        registerComponent(new PlayerDataCommand());

        OpenMinetopia.getCommandManager().getCommandCompletions().registerCompletion("playerDataCategories", context -> {
            List<String> categories = new ArrayList<>(List.of("all"));
            PlayerDataCategory.available().stream().map(PlayerDataCategory::key).forEach(categories::add);
            return categories;
        });
    }
}
