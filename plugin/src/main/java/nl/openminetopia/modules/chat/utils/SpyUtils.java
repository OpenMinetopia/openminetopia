package nl.openminetopia.modules.chat.utils;

import lombok.experimental.UtilityClass;
import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.api.player.PlayerManager;
import nl.openminetopia.api.player.objects.MinetopiaPlayer;
import nl.openminetopia.configuration.DefaultConfiguration;
import nl.openminetopia.configuration.MessageConfiguration;
import nl.openminetopia.utils.ChatUtils;
import nl.openminetopia.utils.webhooks.DiscordWebhook;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.awt.*;
import java.time.Year;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@UtilityClass
public class SpyUtils {

    private enum SpyType {
        CHAT,
        COMMAND
    }

    public void chatSpy(Player player, String message, List<Player> ignore) {
        spyToDiscord(SpyType.CHAT, player, message);

        String spiedMessage = MessageConfiguration.message("chat_chatspy_format")
                .replace("<player>", player.getName())
                .replace("<message>", message);

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (onlinePlayer.getUniqueId().equals(player.getUniqueId())) continue;
            if (ignore.contains(onlinePlayer)) continue;

            MinetopiaPlayer minetopiaPlayer = PlayerManager.getInstance().getOnlineMinetopiaPlayer(onlinePlayer);
            if (minetopiaPlayer == null) return;

            if (!minetopiaPlayer.isChatSpyEnabled()) continue;
            DefaultConfiguration configuration = OpenMinetopia.getDefaultConfiguration();
            if (onlinePlayer.getWorld().equals(player.getWorld()) &&
                    onlinePlayer.getLocation().distance(player.getLocation()) < configuration.getChatRadiusRange()) continue;
            ChatUtils.sendFormattedMessage(minetopiaPlayer, spiedMessage);
        }
    }

    public void commandSpy(Player player, String command) {
        spyToDiscord(SpyType.COMMAND, player, command);

        String spiedMessage = MessageConfiguration.message("chat_commandspy_format")
                .replace("<player>", player.getName())
                .replace("<command>", command);

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (onlinePlayer.getUniqueId().equals(player.getUniqueId())) continue;

            MinetopiaPlayer minetopiaPlayer = PlayerManager.getInstance().getOnlineMinetopiaPlayer(onlinePlayer);
            if (minetopiaPlayer == null) return;

            if (minetopiaPlayer.isCommandSpyEnabled()) ChatUtils.sendFormattedMessage(minetopiaPlayer, spiedMessage);
        }
    }

    private void spyToDiscord(SpyType type, Player player, String content) {
        DefaultConfiguration configuration = OpenMinetopia.getDefaultConfiguration();
        String webhookUrl = (type == SpyType.CHAT) ? configuration.chatSpyWebhookUrl : configuration.commandSpyWebhookUrl;

        if (webhookUrl == null || webhookUrl.isEmpty()) return;

        CompletableFuture.runAsync(() -> {
            try {
                DiscordWebhook webhook = new DiscordWebhook(webhookUrl);
                DiscordWebhook.EmbedObject embed = new DiscordWebhook.EmbedObject()
                        .setTitle(type == SpyType.CHAT ? "Chat Spy" : "Command Spy")
                        .addField(type == SpyType.CHAT ? "Message: " : "Command: ", content, false)
                        .setColor(type == SpyType.CHAT ? Color.BLUE : Color.RED)
                        .setFooter("OpenMinetopia ©️ " + Year.now().getValue(), "https://avatars.githubusercontent.com/u/185693104")
                        .setAuthor(player.getName(), "", "https://mc-heads.net/avatar/" + player.getUniqueId());

                webhook.addEmbed(embed);
                webhook.execute();
            } catch (Exception e) {
                OpenMinetopia.getInstance().getLogger().warning("Failed to send spy message to Discord: " + e.getMessage());
            }
        });
    }
}
