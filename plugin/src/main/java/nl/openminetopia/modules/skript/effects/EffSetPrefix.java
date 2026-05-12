package nl.openminetopia.modules.skript.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import nl.openminetopia.api.player.PlayerManager;
import nl.openminetopia.api.player.objects.MinetopiaPlayer;
import nl.openminetopia.modules.prefix.objects.Prefix;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import javax.annotation.Nullable;

public class EffSetPrefix extends Effect {

    static {
        Skript.registerEffect(EffSetPrefix.class, "(omt|openminetopia) set active prefix of %players% to %-string%");
    }

    private Expression<Player> player;
    private Expression<String> prefix;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parser) {
        this.player = (Expression<Player>) expressions[0];
        this.prefix = (Expression<String>) expressions[1];
        return true;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "Set active prefix effect with expression player" + player.toString(event, debug) + " and prefix string expression" + prefix.toString(event, debug);
    }
    @Override
    protected void execute(Event event) {
        for (Player user : player.getAll(event)) {
            MinetopiaPlayer minetopiaPlayer = PlayerManager.getInstance().getOnlineMinetopiaPlayer(user);
            if (minetopiaPlayer == null) continue;
            String parsedPrefix = prefix.getSingle(event);
            for (Prefix prefix : minetopiaPlayer.getPrefixes()) {
                if (prefix.getPrefix().equalsIgnoreCase(parsedPrefix)) {
                    minetopiaPlayer.setActivePrefix(prefix);
                    break;
                }
            }
        }
    }
}
