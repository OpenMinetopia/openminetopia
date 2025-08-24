package nl.openminetopia.modules.chat;

import com.jazzkuh.modulemanager.spigot.SpigotModule;
import com.jazzkuh.modulemanager.spigot.SpigotModuleManager;
import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.modules.chat.listeners.PlayerChatListener;
import nl.openminetopia.modules.chat.listeners.PlayerCommandListener;
import nl.openminetopia.modules.data.DataModule;
import nl.openminetopia.utils.FeatureUtils;
import org.jetbrains.annotations.NotNull;

public class ChatModule extends SpigotModule<@NotNull OpenMinetopia> {

    public ChatModule(SpigotModuleManager<@NotNull OpenMinetopia> moduleManager, DataModule dataModule) {
        super(moduleManager);
    }

    @Override
    public void onEnable() {
        // Check if chat feature is enabled
        if (FeatureUtils.isFeatureDisabled("chat")) {
            getLogger().info("Chat feature is disabled in config.yml");
            return;
        }

        registerComponent(new PlayerChatListener());
        registerComponent(new PlayerCommandListener());
    }

    @Override
    public void onDisable() {
        // Unregister listeners and commands
    }
}
