package nl.openminetopia.modules.chat.listeners;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
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
import java.util.HashSet;
import java.util.Set;

public class PlayerChatListener implements Listener {

    @EventHandler
    public void playerChat(AsyncChatEvent event) {
        if (event.isCancelled()) return;

        Player source = event.getPlayer();
        MinetopiaPlayer minetopiaPlayer = PlayerManager.getInstance().getOnlineMinetopiaPlayer(source);
        if (minetopiaPlayer == null) return;

        DefaultConfiguration configuration = OpenMinetopia.getDefaultConfiguration();

        String plainMessage = PlainTextComponentSerializer.plainText().serialize(event.message());
        SpyUtils.chatSpy(source, plainMessage, new ArrayList<>());

        if (!configuration.isChatEnabled()) return;
        if (!minetopiaPlayer.isInPlace()) return;
        if (minetopiaPlayer.isStaffchatEnabled()) return;

        PoliceModule policeModule = OpenMinetopia.getModuleManager().get(PoliceModule.class);
        if (policeModule.getWalkieTalkieManager().isPoliceChatEnabled(source)) return;

        Set<Player> recipients = new HashSet<>();
        if (configuration.isChatRadiusEnabled()) {
            double range = configuration.getChatRadiusRange();
            for (Player target : Bukkit.getOnlinePlayers()) {
                if (target.equals(source)) continue;
                if (target.getWorld().equals(source.getWorld())
                        && source.getLocation().distance(target.getLocation()) <= range) {
                    recipients.add(target);
                }
            }
            if (recipients.isEmpty() && configuration.isNotifyWhenNobodyInRange()) {
                source.sendMessage(MessageConfiguration.component("chat_no_players_in_range"));
                event.setCancelled(true);
                return;
            }
            recipients.add(source);
        } else {
            recipients.addAll(Bukkit.getOnlinePlayers());
        }

        event.viewers().removeIf(viewer -> viewer instanceof Player p && !recipients.contains(p));

        String formattedMessage = configuration.getChatFormat();
        if (BalaclavaUtils.isBalaclavaItem(source.getInventory().getHelmet())) {
            formattedMessage = formattedMessage
                    .replace("<level>", configuration.getDefaultLevel() + "")
                    .replace("<prefix>", configuration.getDefaultPrefix())
                    .replace("<name_color>", configuration.getDefaultNameColor())
                    .replace("<level_color>", configuration.getDefaultLevelColor())
                    .replace("<prefix_color>", configuration.getDefaultPrefixColor())
                    .replace("<chat_color>", configuration.getDefaultChatColor());
        }

        int messageIndex = formattedMessage.indexOf("<message>");
        String formatBeforeMessage = messageIndex >= 0
                ? formattedMessage.substring(0, messageIndex + "<message>".length())
                : formattedMessage;
        String formatAfterMessage = messageIndex >= 0 && messageIndex + "<message>".length() < formattedMessage.length()
                ? formattedMessage.substring(messageIndex + "<message>".length())
                : "";

        Component baseComponent = ChatUtils.format(minetopiaPlayer, formatBeforeMessage).replaceText(
                builder -> builder.matchLiteral("<message>").replacement(Component.empty())
        );
        Component afterMessageComponent = formatAfterMessage.isEmpty()
                ? Component.empty()
                : ChatUtils.format(minetopiaPlayer, formatAfterMessage);

        String chatColor = minetopiaPlayer.getActiveChatColor().color();
        String escapedMessage = MiniMessage.miniMessage().escapeTags(plainMessage);
        Component defaultMessageComponent = ChatUtils.color(chatColor + escapedMessage);

        event.renderer((src, srcDisplayName, msg, viewer) -> {
            Component messageComponent = defaultMessageComponent;
            if (viewer instanceof Player target && plainMessage.contains(target.getName())) {
                String highlighted = escapedMessage.replace(
                        target.getName(),
                        "<green>" + target.getName() + "</green>" + chatColor
                );
                messageComponent = ChatUtils.color(chatColor + highlighted);
                target.playSound(target.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
            }
            return baseComponent.append(messageComponent).append(afterMessageComponent);
        });
    }
}
