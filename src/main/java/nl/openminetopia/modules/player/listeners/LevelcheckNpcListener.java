package nl.openminetopia.modules.player.listeners;

import com.jazzkuh.inventorylib.objects.Menu;
import com.jazzkuh.inventorylib.objects.icon.Icon;
import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.api.player.PlayerManager;
import nl.openminetopia.api.player.objects.MinetopiaPlayer;
import nl.openminetopia.configuration.MessageConfiguration;
import nl.openminetopia.modules.banking.BankingModule;
import nl.openminetopia.modules.banking.models.BankAccountModel;
import nl.openminetopia.modules.player.PlayerModule;
import nl.openminetopia.modules.player.utils.LevelUtil;
import nl.openminetopia.utils.ChatUtils;
import nl.openminetopia.utils.item.ItemBuilder;
import nl.openminetopia.utils.wrappers.CustomNpcClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class LevelcheckNpcListener implements Listener {

    @EventHandler
    public void clickNpc(final CustomNpcClickEvent event) {
        if (event.getClickType() != CustomNpcClickEvent.ClickType.RIGHT_CLICK) return;
        if (!event.getNpcName().contains("Levelcheck")) return;

        event.setCancelled(true);

        Player player = event.getClicker();
        MinetopiaPlayer minetopiaPlayer = PlayerManager.getInstance().getOnlineMinetopiaPlayer(player);
        if (minetopiaPlayer == null) return;

        int level = minetopiaPlayer.getLevel();
        int calculatedLevel = minetopiaPlayer.getCalculatedLevel();

        if (level == calculatedLevel) {
            ChatUtils.sendFormattedMessage(minetopiaPlayer, MessageConfiguration.message("levelcheck_already_at_level"));
            return;
        }

        PlayerModule playerModule = OpenMinetopia.getModuleManager().get(PlayerModule.class);
        if (!playerModule.getConfiguration().isLevelUpCostEnabled()) {
            minetopiaPlayer.setLevel(calculatedLevel);
            return;
        }

        LevelUtil.calculateLevelupCosts(level, calculatedLevel).whenComplete((cost, throwable) -> {
            if (cost == null || throwable != null) return;
            Bukkit.getScheduler().runTask(OpenMinetopia.getInstance(), () -> new LevelCheckMenu(minetopiaPlayer, calculatedLevel, cost).open(player));
        });
    }

    private static class LevelCheckMenu extends Menu {
        public LevelCheckMenu(MinetopiaPlayer minetopiaPlayer, int newLevel, double cost) {
            super(ChatUtils.color("<gold>Levelcheck"), 3);

            BankingModule bankingModule = OpenMinetopia.getModuleManager().get(BankingModule.class);
            if (bankingModule == null) return;

            // Check if the player has enough money
            BankAccountModel bankAccountModel = bankingModule.getAccountById(minetopiaPlayer.getUuid());
            boolean canAfford = bankAccountModel.getBalance() >= cost;

            // Akkoord-knop
            addItem(new Icon(11, new ItemBuilder(Material.GREEN_WOOL)
                    .setName("<green>Akkoord")
                    .addLoreLine(" ")
                    .addLoreLine("<dark_green>Klik hier om je level te upgraden naar <green>Level " + newLevel + "<dark_green>!")
                    .addLoreLine("<dark_green>Deze upgrade kost <green>" + (cost == 0 ? "niks" : "€" + cost))
                    .toItemStack(), event -> {
                        if (!canAfford) {
                            ChatUtils.sendFormattedMessage(minetopiaPlayer, MessageConfiguration.message("levelcheck_not_enough_money"));
                            return;
                        }

                        bankAccountModel.setBalance(bankAccountModel.getBalance() - cost);
                        minetopiaPlayer.setLevel(newLevel);
                        ChatUtils.sendFormattedMessage(minetopiaPlayer, MessageConfiguration.message("levelcheck_success")
                                .replace("<level>", String.valueOf(newLevel)));
                        minetopiaPlayer.save();

                        player.getOpenInventory().close();
                    }));

            // Annuleer-knop
            addItem(new Icon(15, new ItemBuilder(Material.RED_WOOL)
                    .setName("<red>Annuleren")
                    .addLoreLine(" ")
                    .addLoreLine("<dark_red>Klik om deze actie te <red>annuleren</red>.")
                    .toItemStack(), event -> player.getOpenInventory().close()));
        }
    }
}