package nl.openminetopia.modules.banking.stattop;

import lombok.RequiredArgsConstructor;
import nl.openminetopia.api.player.objects.MinetopiaPlayer;
import nl.openminetopia.api.stattop.StatTopEntry;
import nl.openminetopia.api.stattop.StatTopType;
import nl.openminetopia.modules.banking.BankingModule;
import nl.openminetopia.modules.banking.enums.AccountType;
import nl.openminetopia.modules.banking.models.BankAccountModel;
import nl.openminetopia.modules.data.utils.StormUtils;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
public class BalanceStatTopType implements StatTopType {

    private final BankingModule bankingModule;

    @Override
    public String key() {
        return "balance";
    }

    @Override
    public String displayName() {
        return "Saldo";
    }

    @Override
    public CompletableFuture<List<StatTopEntry>> stored(int limit) {
        return StormUtils.queryTop("SELECT uuid, balance AS score FROM banking_accounts WHERE type = ? ORDER BY balance DESC LIMIT ?",
                AccountType.PRIVATE.toString(), limit);
    }

    @Override
    public Double live(MinetopiaPlayer player) {
        BankAccountModel account = bankingModule.getAccountById(player.getUuid());
        if (account == null || account.getType() != AccountType.PRIVATE) return null;
        return account.getBalance();
    }

    @Override
    public String format(double value) {
        return bankingModule.format(value);
    }
}
