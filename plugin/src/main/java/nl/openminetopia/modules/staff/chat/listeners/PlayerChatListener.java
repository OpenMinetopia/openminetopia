package nl.openminetopia.modules.staff.chat.listeners;

import io.papermc.paper.event.player.AsyncChatEvent;
import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.api.player.PlayerManager;
import nl.openminetopia.api.player.objects.MinetopiaPlayer;
import nl.openminetopia.configuration.MessageConfiguration;
import nl.openminetopia.modules.police.PoliceModule;
import nl.openminetopia.utils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PlayerChatListener implements Listener {

    @EventHandler
    public void onChat(AsyncChatEvent event) {
        Player source = event.getPlayer();

        PoliceModule policeModule = OpenMinetopia.getModuleManager().get(PoliceModule.class);
        if (policeModule.getWalkieTalkieManager().isPoliceChatEnabled(source)) return;

        MinetopiaPlayer minetopiaPlayer = PlayerManager.getInstance().getOnlineMinetopiaPlayer(source);
        if (minetopiaPlayer == null) return;

        if (!source.hasPermission("openminetopia.staffchat") && minetopiaPlayer.isStaffchatEnabled()) {
            minetopiaPlayer.setStaffchatEnabled(false);
            return;
        }
        if (!source.hasPermission("openminetopia.staffchat") || !minetopiaPlayer.isStaffchatEnabled()) return;

        event.setCancelled(true);

        String formattedMessage = MessageConfiguration.message("staff_chat_format")
                .replace("<player>", source.getName())
                .replace("<world_name>", source.getWorld().getName())
                .replace("<message>", ChatUtils.stripMiniMessage(event.message()));

        Bukkit.getServer().getOnlinePlayers().forEach(target -> {
            if (target.hasPermission("openminetopia.staffchat")) {
                target.sendMessage(ChatUtils.format(minetopiaPlayer, formattedMessage));
            }
        });

        Bukkit.getConsoleSender().sendMessage(ChatUtils.format(minetopiaPlayer, formattedMessage));
    }
}
