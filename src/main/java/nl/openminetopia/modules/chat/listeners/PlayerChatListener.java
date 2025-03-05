package nl.openminetopia.modules.chat.listeners;

import io.papermc.paper.event.player.AsyncChatEvent;
import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.api.player.PlayerManager;
import nl.openminetopia.api.player.objects.MinetopiaPlayer;
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
        if (event.isCancelled()) return;

        Player source = event.getPlayer();
        MinetopiaPlayer minetopiaPlayer = PlayerManager.getInstance().getOnlineMinetopiaPlayer(source);

        if (minetopiaPlayer == null) return;

        DefaultConfiguration configuration = OpenMinetopia.getDefaultConfiguration();

        if (!configuration.isChatEnabled()) return;
        if (!minetopiaPlayer.isInPlace()) return;
        if (minetopiaPlayer.isStaffchatEnabled()) return;

        PoliceModule policeModule = OpenMinetopia.getModuleManager().get(PoliceModule.class);
        if (policeModule.getWalkieTalkieManager().isPoliceChatEnabled(source)
                || policeModule.getWalkieTalkieManager().isComposingMessage(source)) return;

        event.setCancelled(true);

        List<Player> recipients = new ArrayList<>();

        if (configuration.isChatRadiusEnabled()) {
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
        } else {
            recipients.addAll(Bukkit.getOnlinePlayers());
        }

        // Format the message
        String originalMessage = ChatUtils.rawMiniMessage(event.message());
        String formattedMessage = configuration.getChatFormat();

        SpyUtils.chatSpy(source, originalMessage, recipients);

        // Replace <message> placeholder with original message
        String finalMessage = formattedMessage.replace("<message>", originalMessage);

        // Check if the player is wearing a balaclava and replace placeholders with default values
        if (BalaclavaUtils.isBalaclavaItem(source.getInventory().getHelmet())) {
            finalMessage = finalMessage
                    .replace("<level>", configuration.getDefaultLevel() + "")
                    .replace("<prefix>", configuration.getDefaultPrefix())
                    .replace("<name_color>", configuration.getDefaultNameColor())
                    .replace("<level_color>", configuration.getDefaultLevelColor())
                    .replace("<prefix_color>", configuration.getDefaultPrefixColor())
                    .replace("<chat_color>", configuration.getDefaultChatColor());
        }

        Bukkit.getConsoleSender().sendMessage(ChatUtils.format(minetopiaPlayer, finalMessage)); // Log the message without potential scrambled name

        for (Player target : recipients) {
            // Check if the target's name is in the original message and highlight it
            if (originalMessage.contains(target.getName())) {
                String highlightedMessage = originalMessage.replace(target.getName(), "<green>" + target.getName() + "<white>");
                finalMessage = formattedMessage.replace("<message>", highlightedMessage);

                // Play sound for the mentioned target
                target.playSound(target.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
            }

            // Send the formatted message to the target
            target.sendMessage(ChatUtils.format(minetopiaPlayer, finalMessage));
        }
    }
}
