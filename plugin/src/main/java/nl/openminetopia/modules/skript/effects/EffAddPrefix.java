package nl.openminetopia.modules.skript.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.util.Timespan;
import ch.njol.util.Kleenean;
import nl.openminetopia.api.player.PlayerManager;
import nl.openminetopia.api.player.objects.MinetopiaPlayer;
import nl.openminetopia.modules.prefix.objects.Prefix;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import javax.annotation.Nullable;

public class EffAddPrefix extends Effect {

    static {
        Skript.registerEffect(
                EffAddPrefix.class,
                "(omt|openminetopia) add prefix %string% to %players% [(for|with) %-timespan%]"
        );
    }

    private Expression<String> prefix;
    private Expression<Player> players;
    private Expression<Timespan> expiry;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parser) {
        this.prefix = (Expression<String>) expressions[0];
        this.players = (Expression<Player>) expressions[1];
        this.expiry = (Expression<Timespan>) expressions[2];
        return true;
    }

    @Override
    protected void execute(Event event) {
        String parsedPrefix = prefix.getSingle(event);
        if (parsedPrefix == null) return;

        long expiresAt = -1;

        if (expiry != null) {
            Timespan ts = expiry.getSingle(event);
            if (ts != null) {
                expiresAt = System.currentTimeMillis() + ts.getAs(Timespan.TimePeriod.MILLISECOND);
            }
        }

        for (Player player : players.getAll(event)) {
            MinetopiaPlayer mtPlayer = PlayerManager.getInstance().getOnlineMinetopiaPlayer(player);
            if (mtPlayer == null) continue;
            mtPlayer.addPrefix(new Prefix(parsedPrefix, expiresAt));
        }
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "Add prefix effect with expression player" + players.toString(event, debug)
                + " and prefix string expression" + prefix.toString(event, debug)
                + " and expiry timespan expression" + (expiry != null ? expiry.toString(event, debug) : "null");
    }
}