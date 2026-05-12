package nl.openminetopia.modules.police.handcuff;

import lombok.Getter;
import nl.openminetopia.api.player.objects.MinetopiaPlayer;
import nl.openminetopia.modules.police.handcuff.objects.HandcuffedPlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class HandcuffManager {

    @Getter
    private static final HandcuffManager instance = new HandcuffManager();

    @Getter
    public List<HandcuffedPlayer> handcuffedPlayers = new ArrayList<>();

    public void handcuff(MinetopiaPlayer targetMinetopiaPlayer, MinetopiaPlayer sourceMinetopiaPlayer) {
        Player target = targetMinetopiaPlayer.getBukkit().getPlayer();
        if (target == null) return;

        Player source = sourceMinetopiaPlayer.getBukkit().getPlayer();
        if (source == null) return;

        HandcuffedPlayer handcuffedPlayer = new HandcuffedPlayer(source, target);
        handcuffedPlayers.add(handcuffedPlayer);

        handcuffedPlayer.handcuff();
    }

    public void release(HandcuffedPlayer handcuffedPlayer) {
        handcuffedPlayer.release();
        handcuffedPlayers.remove(handcuffedPlayer);
    }

    public boolean isHandcuffed(Player player) {
        return handcuffedPlayers.stream().anyMatch(handcuffedPlayer -> handcuffedPlayer.getPlayer().equals(player));
    }

    public boolean isHandcuffing(Player player) {
        return handcuffedPlayers.stream().anyMatch(handcuffedPlayer -> handcuffedPlayer.getSource().equals(player));
    }

    public HandcuffedPlayer getHandcuffedPlayer(Player player) {
        return handcuffedPlayers.stream().filter(handcuffedPlayer -> handcuffedPlayer.getPlayer().equals(player)).findFirst().orElse(null);
    }
}
