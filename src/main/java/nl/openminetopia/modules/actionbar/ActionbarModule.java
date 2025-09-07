package nl.openminetopia.modules.actionbar;

import com.jazzkuh.modulemanager.spigot.SpigotModule;
import com.jazzkuh.modulemanager.spigot.SpigotModuleManager;
import lombok.Getter;
import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.api.player.PlayerManager;
import nl.openminetopia.modules.actionbar.commands.ActionbarCommand;
import nl.openminetopia.modules.actionbar.listeners.ActionbarJoinListener;
import nl.openminetopia.modules.actionbar.runnables.ActionbarRunnable;
import nl.openminetopia.modules.data.DataModule;
import org.jetbrains.annotations.NotNull;

public class ActionbarModule extends SpigotModule<@NotNull OpenMinetopia> {

    @Getter
    private ActionbarRunnable actionbarRunnable;

    public ActionbarModule(SpigotModuleManager<@NotNull OpenMinetopia> moduleManager, DataModule dataModule) {
        super(moduleManager);
    }

    @Override
    public void onEnable() {
        registerComponent(new ActionbarJoinListener());
        registerComponent(new ActionbarCommand());

        this.actionbarRunnable = new ActionbarRunnable(PlayerManager.getInstance(), 1000, 100, 1500, () -> PlayerManager.getInstance().getOnlinePlayers().keySet().stream().toList());
        OpenMinetopia.getInstance().registerDirtyPlayerRunnable(actionbarRunnable, 20L);
    }

    @Override
    public void onDisable() {
        OpenMinetopia.getInstance().unregisterDirtyPlayerRunnable(actionbarRunnable);
    }
}
