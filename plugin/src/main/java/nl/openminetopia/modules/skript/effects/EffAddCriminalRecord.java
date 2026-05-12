package nl.openminetopia.modules.skript.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import nl.openminetopia.api.player.PlayerManager;
import nl.openminetopia.api.player.objects.MinetopiaPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import javax.annotation.Nullable;

public class  EffAddCriminalRecord extends Effect {

    static {
        Skript.registerEffect(EffAddCriminalRecord.class, "(omt|openminetopia) add criminalrecord to %players% (by reason of|because [of]|on account of|due to|for|with reason) %-string% by %players%");
    }

    private Expression<Player> player;
    private Expression<String> reason;
    private Expression<Player> executor;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parser) {
        this.player = (Expression<Player>) expressions[0];
        this.reason = (Expression<String>) expressions[1];
        this.executor = (Expression<Player>) expressions[2];
        return true;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "Add CriminalRecord effect with expression player" + player.toString(event, debug) + " and reason string expression" + reason.toString(event, debug) + " and executor player expression" + executor.toString(event, debug);
    }
    @Override
    protected void execute(Event event) {
        for (Player user : player.getAll(event)) {
            MinetopiaPlayer minetopiaPlayer = PlayerManager.getInstance().getOnlineMinetopiaPlayer(user);
            if (minetopiaPlayer == null) continue;
            Player executorPlayer = executor.getSingle(event);
            if (executorPlayer == null) continue;
            minetopiaPlayer.addCriminalRecord(reason.getSingle(event), executorPlayer.getUniqueId(), System.currentTimeMillis());

        }
    }
}
