package nl.openminetopia.api.player;

import lombok.Getter;
import net.megavex.scoreboardlibrary.api.ScoreboardLibrary;
import net.megavex.scoreboardlibrary.api.sidebar.Sidebar;
import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.api.player.objects.MinetopiaPlayer;
import nl.openminetopia.modules.scoreboard.ScoreboardModule;
import nl.openminetopia.utils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

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

    private final ScoreboardLibrary scoreboardLibrary = OpenMinetopia.getModuleManager().get(ScoreboardModule.class).getScoreboardLibrary();

    public void updateBoard(MinetopiaPlayer minetopiaPlayer) {
        Sidebar sidebar = getScoreboard(minetopiaPlayer.getUuid());
        if (sidebar == null) return;
        if (!minetopiaPlayer.isScoreboardVisible()) return;

        Player player = minetopiaPlayer.getBukkit().getPlayer();
        if (player == null) return;

        if (!minetopiaPlayer.isInPlace()) {
            if (sidebar.players().contains(player)) removeScoreboard(player);
            return;
        }
        if (!sidebar.players().contains(player)) addScoreboard(player);

        List<String> lines = OpenMinetopia.getDefaultConfiguration().getScoreboardLines();
        int size = Math.min(lines.size(), 16);
        for (int i = 0; i < size; i++) {
            String line = lines.get(i);
            if (i == 0) {
                sidebar.title(ChatUtils.format(minetopiaPlayer, line));
            } else {
                sidebar.line(i - 1, ChatUtils.format(minetopiaPlayer, line));
            }
        }
    }

    public void addScoreboard(Player player) {
        if (!OpenMinetopia.getDefaultConfiguration().isScoreboardEnabled()) return;
        if (scoreboards.containsKey(player.getUniqueId())) return;

        Sidebar sidebar = scoreboardLibrary.createSidebar();

        sidebar.addPlayer(player);
        scoreboards.put(player.getUniqueId(), sidebar);
    }

    public void removeScoreboard(Player player) {
        removeScoreboard(player.getUniqueId());
    }

    public void removeScoreboard(UUID uuid) {
        Sidebar sidebar = scoreboards.remove(uuid);
        if (sidebar != null) {
            Player p = Bukkit.getPlayer(uuid);
            if (p != null) sidebar.removePlayer(p);
            sidebar.close();
        }
    }

    public Sidebar getScoreboard(UUID uuid) {
        return scoreboards.get(uuid);
    }
}
