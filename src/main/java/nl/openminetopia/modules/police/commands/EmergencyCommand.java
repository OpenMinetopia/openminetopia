package nl.openminetopia.modules.police.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.api.player.PlayerManager;
import nl.openminetopia.api.player.objects.MinetopiaPlayer;
import nl.openminetopia.configuration.MessageConfiguration;
import nl.openminetopia.modules.police.PoliceModule;
import nl.openminetopia.utils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("112|911")
public class EmergencyCommand extends BaseCommand {

    @Default
    public void emergency(CommandSender sender, String message) {
        if (!(sender instanceof Player player)) {
            broadcastEmergency(message, sender);
            return;
        }

        if (hasCooldown(player)) {
            MinetopiaPlayer minetopiaPlayer = PlayerManager.getInstance().getOnlineMinetopiaPlayer(player);

            long cooldown = OpenMinetopia.getModuleManager().get(PoliceModule.class).getEmergencyCooldowns().get(player.getUniqueId());
            ChatUtils.sendFormattedMessage(minetopiaPlayer, MessageConfiguration.message("emergency_too_soon"));
            ChatUtils.sendFormattedMessage(minetopiaPlayer, MessageConfiguration.message("emergency_cooldown")
                    .replace("<time>", cooldownToTime(cooldown)));
            return;
        }

        OpenMinetopia.getModuleManager().get(PoliceModule.class).getEmergencyCooldowns().put(player.getUniqueId(), System.currentTimeMillis());
        broadcastEmergency(message, sender);
    }

    private void broadcastEmergency(String message, CommandSender sender) {
        boolean isPlayer = sender instanceof Player;
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (!onlinePlayer.hasPermission("openminetopia.police")) return;

            MinetopiaPlayer minetopiaPlayer = PlayerManager.getInstance().getOnlineMinetopiaPlayer(onlinePlayer);

            ChatUtils.sendFormattedMessage(minetopiaPlayer, MessageConfiguration.message("emergency_format")
                    .replace("<sender>", isPlayer ? sender.getName() : "Anoniem")
                    .replace("<x>", isPlayer ? getX(sender) : "")
                    .replace("<y>", isPlayer ? getY(sender) : "")
                    .replace("<z>", isPlayer ? getZ(sender) : "")
                    .replace("<world>", isPlayer ? getWorld(sender) : "Onbekend")
                    .replace("<message>", message));
        }
    }

    private String getX(CommandSender sender) {
        if (!(sender instanceof Player player)) return "";
        return player.getLocation().getBlockX() + "";
    }

    private String getY(CommandSender sender) {
        if (!(sender instanceof Player player)) return "";
        return player.getLocation().getBlockY() + "";
    }

    private String getZ(CommandSender sender) {
        if (!(sender instanceof Player player)) return "";
        return player.getLocation().getBlockZ() + "";
    }

    private String getWorld(CommandSender sender) {
        if (!(sender instanceof Player player)) return "";
        return player.getWorld().getName();
    }

    private boolean hasCooldown(Player player) {
        PoliceModule policeModule = OpenMinetopia.getModuleManager().get(PoliceModule.class);

        if (!policeModule.getEmergencyCooldowns().containsKey(player.getUniqueId())) return false;

        long cooldown = policeModule.getEmergencyCooldowns().get(player.getUniqueId());
        // check if cooldown of 5 minutes has passed and remove from map if so
        if (System.currentTimeMillis() - cooldown >= (OpenMinetopia.getDefaultConfiguration().getEmergencyCooldown() * 1000L)) {
            policeModule.getEmergencyCooldowns().remove(player.getUniqueId());
            return false;
        }
        return true;
    }

    private String cooldownToTime(long cooldown) {
        long millis = (OpenMinetopia.getDefaultConfiguration().getEmergencyCooldown() * 1000L) - (System.currentTimeMillis() - cooldown);
        long seconds = millisToSeconds(millis);
        long minutes = millisToMinutes(millis);
        return minutes + " minuten en " + seconds + " seconden";
    }

    private long millisToSeconds(long millis) {
        return millis / 1000;
    }

    private long millisToMinutes(long millis) {
        return millis / 60000;
    }
}
