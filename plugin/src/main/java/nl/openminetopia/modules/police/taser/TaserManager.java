package nl.openminetopia.modules.police.taser;

import lombok.Getter;
import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.api.player.objects.MinetopiaPlayer;
import nl.openminetopia.modules.police.utils.TaserUtils;
import nl.openminetopia.utils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class TaserManager {

    private final List<UUID> taseredPlayers = new ArrayList<>();

    public void taser(MinetopiaPlayer targetMinetopiaPlayer) {
        Player target = targetMinetopiaPlayer.getBukkit().getPlayer();
        if (target == null) return;

        taseredPlayers.add(target.getUniqueId());
        Bukkit.getScheduler().runTaskLaterAsynchronously(OpenMinetopia.getInstance(), () -> {
            taseredPlayers.remove(target.getUniqueId());
        }, OpenMinetopia.getDefaultConfiguration().getTaserFreezeDuration() * 20L);
        target.sendMessage(ChatUtils.format(targetMinetopiaPlayer, "<red>Je bent geraakt door een tazer!"));
        TaserUtils.applyTaserEffects(target);
    }

    public boolean isTasered(Player player) {
        return taseredPlayers.stream().anyMatch(uuid -> uuid.equals(player.getUniqueId()));
    }
}
