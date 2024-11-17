package nl.openminetopia.modules.player.listeners;

import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.api.player.PlayerManager;
import nl.openminetopia.api.player.objects.MinetopiaPlayer;
import nl.openminetopia.modules.banking.BankingModule;
import nl.openminetopia.modules.banking.models.BankAccountModel;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        BankingModule bankingModule = OpenMinetopia.getModuleManager().getModule(BankingModule.class);
        BankAccountModel accountModel = bankingModule.getAccountById(player.getUniqueId());
        bankingModule.getBankAccountModels().remove(accountModel);
        accountModel.getSavingTask().saveAndCancel();

        PlayerManager.getInstance().getMinetopiaPlayerAsync(player, minetopiaPlayer -> {
            if (minetopiaPlayer == null) return;

            minetopiaPlayer.save().whenComplete((unused, throwable) -> {
                if (throwable != null) {
                    throwable.printStackTrace();
                    return;
                }
                OpenMinetopia.getInstance().getLogger().info("Saved player data for " + player.getName());
            });

            minetopiaPlayer.getFitness().getRunnable().cancel();
            minetopiaPlayer.getPlaytimeRunnable().cancel();
            minetopiaPlayer.getHealthStatisticRunnable().cancel();
            minetopiaPlayer.getLevelcheckRunnable().cancel();

            PlayerManager.getInstance().getOnlinePlayers().remove(player.getUniqueId());
        }, Throwable::printStackTrace);
    }
}