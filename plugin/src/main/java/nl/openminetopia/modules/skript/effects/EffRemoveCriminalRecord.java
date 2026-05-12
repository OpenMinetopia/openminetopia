package nl.openminetopia.modules.skript.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import nl.openminetopia.api.player.PlayerManager;
import nl.openminetopia.api.player.objects.MinetopiaPlayer;
import nl.openminetopia.modules.police.models.CriminalRecordModel;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import javax.annotation.Nullable;

public class EffRemoveCriminalRecord extends Effect {

    static {
        Skript.registerEffect(EffRemoveCriminalRecord.class, "(omt|openminetopia) remove criminalrecord %integer% from %players%");
    }

    private Expression<Player> player;
    private Expression<Integer> id;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parser) {
        this.player = (Expression<Player>) expressions[1];
        this.id = (Expression<Integer>) expressions[0];
        return true;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "Remove CriminalRecord effect with ID integer expression" + id.toString(event, debug) + " and player expression" + player.toString(event, debug);
    }
    @Override
    protected void execute(Event event) {
        for (Player user : player.getAll(event)) {
            MinetopiaPlayer minetopiaPlayer = PlayerManager.getInstance().getOnlineMinetopiaPlayer(user);
            if (minetopiaPlayer == null) continue;
            for (CriminalRecordModel criminalRecord : minetopiaPlayer.getCriminalRecords()) {
                if (criminalRecord.getId() != id.getSingle(event)) continue;
                minetopiaPlayer.removeCriminalRecord(criminalRecord);
            }
        }
    }
}
