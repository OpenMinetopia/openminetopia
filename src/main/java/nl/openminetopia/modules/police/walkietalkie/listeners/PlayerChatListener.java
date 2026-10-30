package nl.openminetopia.modules.police.walkietalkie.listeners;

import io.papermc.paper.event.player.AsyncChatEvent;
import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.configuration.DefaultConfiguration;
import nl.openminetopia.configuration.MessageConfiguration;
import nl.openminetopia.modules.police.PoliceModule;
import nl.openminetopia.utils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

public class PlayerChatListener implements Listener {

    private final PoliceModule policeModule = OpenMinetopia.getModuleManager().get(PoliceModule.class);

    @EventHandler
    public void onChat(final AsyncChatEvent event) {
        Player source = event.getPlayer();

        if (!policeModule.getWalkieTalkieManager().isPoliceChatEnabled(source)) return;
        event.setCancelled(true);

        DefaultConfiguration configuration = OpenMinetopia.getDefaultConfiguration();
        String message = ChatUtils.stripMiniMessage(event.message());
        if (configuration.isChatFilterEnabled() && !source.hasPermission(configuration.getChatFilterBypassPermission())) {
            message = configuration.getChatFilter().censor(message);
        }

        String formattedMessage = MessageConfiguration.message("police_walkietalkie_format")
                .replace("<player>", source.getName())
                .replace("<world_name>", source.getWorld().getName())
                .replace("<message>", message);

        for (Player recipient : Bukkit.getOnlinePlayers()) {
            if (!recipient.hasPermission("openminetopia.walkietalkie")) continue;
            ChatUtils.sendMessage(recipient, formattedMessage);
        }
        Bukkit.getConsoleSender().sendMessage(ChatUtils.color(formattedMessage));
    }
}