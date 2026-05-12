package nl.openminetopia.modules.chat;

import nl.openminetopia.utils.modules.ExtendedSpigotModule;
import com.jazzkuh.modulemanager.spigot.SpigotModuleManager;
import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.modules.chat.listeners.PlayerChatListener;
import nl.openminetopia.modules.chat.listeners.PlayerCommandListener;
import nl.openminetopia.modules.data.DataModule;
import org.jetbrains.annotations.NotNull;

public class ChatModule extends ExtendedSpigotModule {

    public ChatModule(SpigotModuleManager<@NotNull OpenMinetopia> moduleManager, DataModule dataModule) {
        super(moduleManager);
    }

    @Override
    public void onEnable() {
        registerComponent(new PlayerChatListener());
        registerComponent(new PlayerCommandListener());
    }

    @Override
    public void onDisable() {
        // Unregister listeners and commands
    }
}
