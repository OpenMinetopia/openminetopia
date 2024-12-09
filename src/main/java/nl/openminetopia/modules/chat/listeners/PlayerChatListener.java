package nl.openminetopia.modules.chat.listeners;

import io.papermc.paper.event.player.AsyncChatEvent;
import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.api.player.PlayerManager;
import nl.openminetopia.configuration.DefaultConfiguration;
import nl.openminetopia.configuration.MessageConfiguration;
import nl.openminetopia.modules.chat.utils.SpyUtils;
import nl.openminetopia.modules.police.PoliceModule;
import nl.openminetopia.modules.police.utils.BalaclavaUtils;
import nl.openminetopia.utils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

public class PlayerChatListener implements Listener {

    @EventHandler
    public void playerChat(AsyncChatEvent event) {
        Player source = event.getPlayer();
        PlayerManager.getInstance().getMinetopiaPlayerAsync(source, minetopiaPlayer -> {
            if (minetopiaPlayer == null) return;

            if (!minetopiaPlayer.isInPlace()) return;
            if (minetopiaPlayer.isStaffchatEnabled()) return;

            PoliceModule policeModule = OpenMinetopia.getModuleManager().getModule(PoliceModule.class);
            if (policeModule.getWalkieTalkieManager().isPoliceChatEnabled(source)
                    || policeModule.getWalkieTalkieManager().isComposingMessage(source)) return;

            List<Player> recipients = new ArrayList<>();

            event.setCancelled(true);

            DefaultConfiguration configuration = OpenMinetopia.getDefaultConfiguration();

            Bukkit.getServer().getOnlinePlayers().forEach(target -> {
                if (target.getWorld().equals(source.getWorld())
                        && source.getLocation().distance(target.getLocation()) <= configuration.getChatRadiusRange())
                    recipients.add(target);
            });

            recipients.remove(source);
            if (recipients.isEmpty() && configuration.isNotifyWhenNobodyInRange()) {
                event.getPlayer().sendMessage(MessageConfiguration.component("chat_no_players_in_range"));
                return;
            }
            recipients.add(source);

            // Format the message
            String originalMessage = ChatUtils.rawMiniMessage(event.message());
            String formattedMessage = configuration.getChatFormat();

            SpyUtils.chatSpy(source, originalMessage, recipients);

            // Iterate over recipients
            recipients.forEach(player -> {
                // Replace <message> placeholder with original message
                String finalMessage = formattedMessage.replace("<message>", originalMessage);

                if (BalaclavaUtils.isBalaclavaItem(source.getInventory().getHelmet())) {
                    finalMessage = finalMessage.replace("<level>", configuration.getDefaultLevel() + "")
                            .replace("<prefix>", configuration.getDefaultPrefix())
                            .replace("<name_color>", configuration.getDefaultNameColor())
                            .replace("<level_color>", configuration.getDefaultLevelColor())
                            .replace("<prefix_color>", configuration.getDefaultPrefixColor())
                            .replace("<chat_color>", configuration.getDefaultChatColor());
                }

                // Check if the player's name is in the original message and highlight it
                if (originalMessage.contains(player.getName())) {
                    String highlightedMessage = originalMessage.replace(player.getName(), "<green>" + player.getName() + "<white>");
                    finalMessage = formattedMessage.replace("<message>", highlightedMessage);

                    // Play sound for the mentioned player
                    player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                }

                // Send the formatted message to the player
                player.sendMessage(ChatUtils.format(minetopiaPlayer, finalMessage));
                Bukkit.getConsoleSender().sendMessage(ChatUtils.format(minetopiaPlayer, finalMessage)); // Log the message without potential scrambled name
            });
        }, Throwable::printStackTrace);
    }
}
