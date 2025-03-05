package nl.openminetopia.api.player;

import lombok.Getter;
import net.megavex.scoreboardlibrary.api.sidebar.Sidebar;
import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.api.player.objects.MinetopiaPlayer;
import nl.openminetopia.modules.scoreboard.ScoreboardModule;
import nl.openminetopia.utils.ChatUtils;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Getter
public class ScoreboardManager {

    private static ScoreboardManager instance;

    public static ScoreboardManager getInstance() {
        if (instance == null) {
            instance = new ScoreboardManager();
        }
        return instance;
    }

    public final HashMap<UUID, Sidebar> scoreboards = new HashMap<>();

    public void updateBoard(MinetopiaPlayer minetopiaPlayer) {
        Sidebar sidebar = getScoreboard(minetopiaPlayer.getUuid());
        if (sidebar == null) return;
        if (!minetopiaPlayer.isScoreboardVisible()) return;

        Player player = minetopiaPlayer.getBukkit().getPlayer();
        if (player == null) return;

        if (!minetopiaPlayer.isInPlace()) {
            if (!sidebar.players().contains(player)) return;
            removeScoreboard(player);
            return;
        }
        if (minetopiaPlayer.isInPlace() && !sidebar.players().contains(player) && minetopiaPlayer.isScoreboardVisible()) {
            addScoreboard(player);
        }

        List<String> lines = OpenMinetopia.getDefaultConfiguration().getScoreboardLines();
        int size = Math.min(lines.size(), 16);
        for (int i = 0; i < size; i++) {
            String line = lines.get(i);
            if (i == 0) {
                sidebar.title(ChatUtils.format(minetopiaPlayer, line));
                continue;
            }
            sidebar.line(i - 1, ChatUtils.format(minetopiaPlayer, line));
        }
    }

    public void addScoreboard(Player player) {
        if (!OpenMinetopia.getDefaultConfiguration().isScoreboardEnabled()) return;
        if (scoreboards.containsKey(player.getUniqueId())) return;
        Sidebar sidebar = OpenMinetopia.getModuleManager().get(ScoreboardModule.class).getScoreboardLibrary().createSidebar();
        sidebar.addPlayer(player);
        scoreboards.put(player.getUniqueId(), sidebar);
    }

    public void removeScoreboard(Player player) {
        Sidebar sidebar = getScoreboard(player.getUniqueId());
        if (sidebar == null) return;
        sidebar.removePlayer(player);
        sidebar.close();
        scoreboards.remove(player.getUniqueId());
    }

    public Sidebar getScoreboard(UUID uuid) {
        return scoreboards.get(uuid);
    }
}
