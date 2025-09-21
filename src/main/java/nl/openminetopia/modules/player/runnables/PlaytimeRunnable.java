package nl.openminetopia.modules.player.runnables;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.api.player.objects.MinetopiaPlayer;
import nl.openminetopia.configuration.MessageConfiguration;
import nl.openminetopia.modules.banking.BankingModule;
import nl.openminetopia.modules.banking.models.BankAccountModel;
import nl.openminetopia.modules.player.PlayerModule;
import nl.openminetopia.modules.player.configuration.LevelCheckConfiguration;
import nl.openminetopia.modules.transactions.enums.TransactionType;
import nl.openminetopia.modules.transactions.events.TransactionUpdateEvent;
import nl.openminetopia.utils.ChatUtils;
import nl.openminetopia.utils.events.EventUtils;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class PlaytimeRunnable extends BukkitRunnable {

    private final Player player;
    private final MinetopiaPlayer minetopiaPlayer;

    public PlaytimeRunnable(MinetopiaPlayer minetopiaPlayer) {
        this.minetopiaPlayer = minetopiaPlayer;
        this.player = minetopiaPlayer.getBukkit().getPlayer();
    }

    @Override
    public void run() {
        if (player == null || !player.isOnline()) {
            cancel();
            return;
        }

        if (minetopiaPlayer == null) return;

        int newPlaytime = minetopiaPlayer.getPlaytime() + 1;

        // If the new playtime is a multiple of 60, update the playtime in the database, so it's only updated every minute
        minetopiaPlayer.setPlaytime(newPlaytime, newPlaytime % 60 == 0);

        // If the new playtime is a multiple of the wage interval, give the player their wage
        PlayerModule playerModule = OpenMinetopia.getModuleManager().get(PlayerModule.class);
        if (newPlaytime % playerModule.getConfiguration().getWageInterval() == 0) {
            giveWage();
        }
    }

    private void giveWage() {
        PlayerModule playerModule = OpenMinetopia.getModuleManager().get(PlayerModule.class);
        LevelCheckConfiguration configuration = playerModule.getConfiguration();
        BankingModule bankingModule = OpenMinetopia.getModuleManager().get(BankingModule.class);
        BankAccountModel bankAccountModel = bankingModule.getAccountById(minetopiaPlayer.getUuid());

        if (!configuration.isWageEnabled()) return;
        if (bankAccountModel == null) return;

        double wage;
        if (configuration.getWageOverrides().containsKey(minetopiaPlayer.getLevel())) {
            wage = configuration.getWageOverrides().get(minetopiaPlayer.getLevel());
        } else {
            String wageFormula = configuration.getWageFormula().replace("<level>", "l");
            Expression expression = new ExpressionBuilder(wageFormula).variables("l").build().setVariable("l", minetopiaPlayer.getLevel());
            wage = expression.evaluate();
        }

        TransactionUpdateEvent transactionUpdateEvent = new TransactionUpdateEvent(player.getUniqueId(), playerModule.getName(), TransactionType.DEPOSIT, wage, bankAccountModel, "Wage payment for playtime", System.currentTimeMillis());
        if (EventUtils.callCancellable(transactionUpdateEvent)) return;

        bankAccountModel.setBalance(bankAccountModel.getBalance() + wage);
        ChatUtils.sendFormattedMessage(minetopiaPlayer, MessageConfiguration.message("levelcheck_wage")
                .replace("<amount>", String.valueOf(wage)));
    }
}