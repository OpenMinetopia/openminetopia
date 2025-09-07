package nl.openminetopia.modules.banking.tasks;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.api.player.PlayerManager;
import nl.openminetopia.api.player.objects.MinetopiaPlayer;
import nl.openminetopia.configuration.MessageConfiguration;
import nl.openminetopia.framework.runnables.AbstractDirtyRunnable;
import nl.openminetopia.modules.banking.BankingModule;
import nl.openminetopia.modules.banking.models.BankAccountModel;
import nl.openminetopia.modules.player.PlayerModule;
import nl.openminetopia.modules.player.configuration.LevelCheckConfiguration;
import nl.openminetopia.modules.transactions.enums.TransactionType;
import nl.openminetopia.modules.transactions.events.TransactionUpdateEvent;
import nl.openminetopia.utils.ChatUtils;
import nl.openminetopia.utils.events.EventUtils;

import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

public class WagePaymentTask extends AbstractDirtyRunnable<UUID > {

    private final Supplier<LevelCheckConfiguration> levelCheckConfigurationSupplier;
    private final PlayerModule playerModule = OpenMinetopia.getModuleManager().get(PlayerModule.class);
    private final BankingModule bankingModule = OpenMinetopia.getModuleManager().get(BankingModule.class);
    private final PlayerManager playerManager;

    public WagePaymentTask(Supplier<LevelCheckConfiguration> levelCheckConfigurationSupplier, PlayerManager playerManager, long minIntervalMs, int batch, long heartbeatMs, Supplier<List<UUID>> allKeysSupplier) {
        super(minIntervalMs, batch, heartbeatMs, allKeysSupplier);
        this.levelCheckConfigurationSupplier = levelCheckConfigurationSupplier;
        this.playerManager = playerManager;
    }

    @Override
    protected void process(UUID key) {
        if (!levelCheckConfigurationSupplier.get().isWageEnabled()) return;
        MinetopiaPlayer minetopiaPlayer = playerManager.getOnlinePlayers().get(key);
        if (minetopiaPlayer == null) return;
        long wageTime = this.levelCheckConfigurationSupplier.get().getWageInterval() * 1000L;
        long playerWageTime = minetopiaPlayer.getWageTime();
        long totalPlayTime = minetopiaPlayer.getPlaytime();

        if (playerWageTime + wageTime <= totalPlayTime) {
            giveWage(minetopiaPlayer);
            minetopiaPlayer.setWageTime(totalPlayTime);
        }
    }

    private void giveWage(MinetopiaPlayer minetopiaPlayer) {
        BankAccountModel bankAccountModel = bankingModule.getAccountById(minetopiaPlayer.getUuid());
        LevelCheckConfiguration configuration = levelCheckConfigurationSupplier.get();
        if (bankAccountModel == null) return;

        double wage;
        if (configuration.getWageOverrides().containsKey(minetopiaPlayer.getLevel())) {
            wage = configuration.getWageOverrides().get(minetopiaPlayer.getLevel());
        } else {
            String wageFormula = configuration.getWageFormula().replace("<level>", "l");
            Expression expression = new ExpressionBuilder(wageFormula).variables("l").build().setVariable("l", minetopiaPlayer.getLevel());
            wage = expression.evaluate();
        }

        TransactionUpdateEvent transactionUpdateEvent = new TransactionUpdateEvent(minetopiaPlayer.getUuid(), playerModule.getName(), TransactionType.DEPOSIT, wage, bankAccountModel, "Wage payment for playtime", System.currentTimeMillis());
        if (EventUtils.callCancellable(transactionUpdateEvent)) return;

        bankAccountModel.setBalance(bankAccountModel.getBalance() + wage);
        ChatUtils.sendFormattedMessage(minetopiaPlayer, MessageConfiguration.message("levelcheck_wage")
                .replace("<amount>", String.valueOf(wage)));
    }
}
