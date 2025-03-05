package nl.openminetopia.modules.police.walkietalkie.listeners;

import io.papermc.paper.event.player.AsyncChatEvent;
import nl.openminetopia.OpenMinetopia;
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

        if (policeModule.getWalkieTalkieManager().isComposingMessage(source)) {
            event.setCancelled(true);

            Player target = policeModule.getWalkieTalkieManager().getTarget(source);
            String formattedMessage = MessageConfiguration.message("police_walkietalkie_private_format")
                    .replace("<player>", source.getName())
                    .replace("<target>", target.getName())
                    .replace("<message>", ChatUtils.rawMiniMessage(event.message()));

            ChatUtils.sendMessage(source, formattedMessage);
            ChatUtils.sendMessage(target, formattedMessage);
            Bukkit.getConsoleSender().sendMessage(ChatUtils.color(formattedMessage));

            policeModule.getWalkieTalkieManager().cancelComposeMessage(event.getPlayer());
        }

        if (policeModule.getWalkieTalkieManager().isPoliceChatEnabled(source)) {
            event.setCancelled(true);

            List<Player> recipients = new ArrayList<>();

            policeModule.getWalkieTalkieManager().getPoliceChatPlayers().forEach(uuid -> {
                Player target = Bukkit.getPlayer(uuid);
                recipients.add(target);
            });

            String formattedMessage = MessageConfiguration.message("police_walkietalkie_format")
                    .replace("<player>", source.getName())
                    .replace("<world_name>", source.getWorld().getName())
                    .replace("<message>", ChatUtils.stripMiniMessage(event.message()));

            // Iterate over recipients
            recipients.forEach(player -> {
                // Send the formatted message to the player
                ChatUtils.sendMessage(player, formattedMessage);
            });
            Bukkit.getConsoleSender().sendMessage(ChatUtils.color(formattedMessage));
        }
    }
}