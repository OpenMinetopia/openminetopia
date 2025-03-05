package nl.openminetopia.modules.player.listeners;

import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.api.player.PlayerManager;
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

        BankingModule bankingModule = OpenMinetopia.getModuleManager().get(BankingModule.class);
        BankAccountModel accountModel = bankingModule.getAccountById(player.getUniqueId());

        if(accountModel != null) {
            bankingModule.getBankAccountModels().remove(accountModel);
            accountModel.save();
            accountModel.getSavingTask().cancel();
        }

        PlayerManager.getInstance().getMinetopiaPlayer(player).whenComplete((minetopiaPlayer, throwable) -> {
            if (minetopiaPlayer == null) return;

            minetopiaPlayer.save().whenComplete((unused, throwable1) -> {
                if (throwable1 != null) {
                    OpenMinetopia.getInstance().getLogger().severe("Couldn't save Player (" + player.getName() + "): " + throwable1.getMessage());
                    return;
                }
                OpenMinetopia.getInstance().getLogger().info("Saved player data for " + player.getName());
            });

            minetopiaPlayer.getFitness().getRunnable().cancel();
            minetopiaPlayer.getFitness().getHealthStatisticRunnable().cancel();
            minetopiaPlayer.getPlaytimeRunnable().cancel();
            minetopiaPlayer.getLevelcheckRunnable().cancel();

            PlayerManager.getInstance().getOnlinePlayers().remove(player.getUniqueId());
        });
    }
}