package nl.openminetopia.modules.scoreboard.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import nl.openminetopia.api.player.ScoreboardManager;
import nl.openminetopia.modules.scoreboard.ScoreboardModule;
import org.bukkit.entity.Player;

@CommandAlias("scoreboard|sb")
public class ScoreboardCommand extends BaseCommand {

    private final ScoreboardModule scoreboardModule;

    public ScoreboardCommand(ScoreboardModule scoreboardModule) {
        this.scoreboardModule = scoreboardModule;
    }

    @Default
    public void onCommand(Player player) {
        if (ScoreboardManager.getInstance().getScoreboards().containsKey(player.getUniqueId())) {
            ScoreboardManager.getInstance().removeScoreboard(player);
            scoreboardModule.getScoreboardUpdateRunnable().remove(player.getUniqueId());
            return;
        }
        ScoreboardManager.getInstance().addScoreboard(player);
        scoreboardModule.getScoreboardUpdateRunnable().markDirty(player.getUniqueId());
    }
}
