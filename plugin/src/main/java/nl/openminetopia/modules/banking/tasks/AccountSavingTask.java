package nl.openminetopia.modules.banking.tasks;

import lombok.Getter;
import nl.openminetopia.modules.banking.models.BankAccountModel;
import nl.openminetopia.modules.data.storm.StormDatabase;
import org.bukkit.scheduler.BukkitRunnable;

@Getter
public class AccountSavingTask extends BukkitRunnable {

    private final BankAccountModel account;

    public AccountSavingTask(BankAccountModel account) {
        this.account = account;
    }

    @Override
    public void run() {
        StormDatabase.getInstance().saveStormModel(account);
    }

}
