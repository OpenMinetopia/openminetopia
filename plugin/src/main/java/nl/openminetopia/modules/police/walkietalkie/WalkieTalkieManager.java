package nl.openminetopia.modules.police.walkietalkie;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

@Getter
public class WalkieTalkieManager {

    private final List<UUID> policeChatPlayers = new ArrayList<>();
    private final Map<UUID, Long> emergencyButtonCooldown = new HashMap<>();

    /* -------- Police chat -------- */

    public void setPoliceChatEnabled(Player player, boolean enabled) {
        if (enabled) policeChatPlayers.add(player.getUniqueId());
        else policeChatPlayers.remove(player.getUniqueId());
    }

    public boolean isPoliceChatEnabled(Player player) {
        return policeChatPlayers.contains(player.getUniqueId());
    }

    /* -------- Emergency button -------- */
    public boolean hasCooldown(Player player) {
        if (emergencyButtonCooldown.containsKey(player.getUniqueId())) {
            if (emergencyButtonCooldown.get(player.getUniqueId()) < System.currentTimeMillis()) {
                return true;
            }
            emergencyButtonCooldown.remove(player.getUniqueId());
        }
        return false;
    }
}
