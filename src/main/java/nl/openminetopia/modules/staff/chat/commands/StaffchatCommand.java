package nl.openminetopia.modules.staff.chat.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Optional;
import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.api.player.PlayerManager;
import nl.openminetopia.api.player.objects.MinetopiaPlayer;
import nl.openminetopia.configuration.MessageConfiguration;
import nl.openminetopia.modules.police.PoliceModule;
import nl.openminetopia.utils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@CommandAlias("staffchat|staffc|sc")
public class StaffchatCommand extends BaseCommand {

    private final PoliceModule policeModule = OpenMinetopia.getModuleManager().get(PoliceModule.class);

    @Default
    @CommandPermission("openminetopia.staffchat")
    public void staffchat(Player player, @Optional String message) {
        MinetopiaPlayer minetopiaPlayer = PlayerManager.getInstance().getOnlineMinetopiaPlayer(player);
        if (minetopiaPlayer == null) return;

        if (message == null) {
            minetopiaPlayer.setStaffchatEnabled(!minetopiaPlayer.isStaffchatEnabled());
            player.sendMessage(ChatUtils.color("<gold>Je hebt staffchat nu <yellow>" + (minetopiaPlayer.isStaffchatEnabled() ? "aangezet" : "uitgezet")));
            return;
        }

        String formattedMessage = MessageConfiguration.message("staff_chat_format")
                .replace("<player>", player.getName())
                .replace("<world_name>", player.getWorld().getName())
                .replace("<message>", message);

        Bukkit.getServer().getOnlinePlayers().forEach(target -> {
            MinetopiaPlayer targetMinetopiaPlayer = PlayerManager.getInstance().getOnlineMinetopiaPlayer(target);
            if (targetMinetopiaPlayer == null) return;

            if (!target.hasPermission("openminetopia.staffchat")) return;
            ChatUtils.sendFormattedMessage(targetMinetopiaPlayer, formattedMessage);
        });
    }
}
