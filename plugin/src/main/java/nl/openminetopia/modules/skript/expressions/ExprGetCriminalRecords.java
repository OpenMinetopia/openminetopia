package nl.openminetopia.modules.skript.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import nl.openminetopia.api.player.PlayerManager;
import nl.openminetopia.api.player.objects.MinetopiaPlayer;
import nl.openminetopia.modules.police.models.CriminalRecordModel;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ExprGetCriminalRecords extends SimpleExpression<Integer> {

    static {
        Skript.registerExpression(ExprGetCriminalRecords.class, Integer.class, ExpressionType.COMBINED, "[the] (omt|openminetopia) criminalrecords of %player%");
    }

    private Expression<Player> exprPlayer;

    @Override
    public Class<? extends Integer> getReturnType() {
        return Integer.class;
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parser) {
        exprPlayer = (Expression<Player>) exprs[0];
        return true;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "Criminalrecords expression with player: " + exprPlayer.toString(event, debug);
    }

    @Override
    @Nullable
    protected Integer[] get(Event event) {
        Player player = exprPlayer.getSingle(event);
        if (player == null) return new Integer[0];
        MinetopiaPlayer minetopiaPlayer = PlayerManager.getInstance().getOnlineMinetopiaPlayer(player);
        if (minetopiaPlayer == null) return new Integer[0];

        List<CriminalRecordModel> criminalRecords = minetopiaPlayer.getCriminalRecords();
        return criminalRecords.stream()
                        .map(CriminalRecordModel::getId)
                        .toArray(Integer[]::new);
    }
}
