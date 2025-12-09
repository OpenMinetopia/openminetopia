package nl.openminetopia.modules.player.listeners;

import dev.triumphteam.gui.guis.GuiItem;
import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.api.player.PlayerManager;
import nl.openminetopia.api.player.objects.MinetopiaPlayer;
import nl.openminetopia.configuration.MessageConfiguration;
import nl.openminetopia.modules.banking.BankingModule;
import nl.openminetopia.modules.banking.models.BankAccountModel;
import nl.openminetopia.modules.player.PlayerModule;
import nl.openminetopia.modules.player.events.PlayerLevelChangeEvent;
import nl.openminetopia.modules.player.utils.LevelUtil;
import nl.openminetopia.utils.ChatUtils;
import nl.openminetopia.utils.events.EventUtils;
import nl.openminetopia.utils.item.ItemBuilder;
import nl.openminetopia.utils.menu.Menu;
import nl.openminetopia.utils.wrappers.CustomNpcClickEvent;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class LevelcheckNpcListener implements Listener {

    @EventHandler
    public void clickNpc(final CustomNpcClickEvent event) {
        if (event.getClickType() != CustomNpcClickEvent.ClickType.RIGHT_CLICK) return;
        if (!event.getNpcName().toLowerCase().contains("levelcheck")) return;

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
            PlayerLevelChangeEvent levelChangeEvent = new PlayerLevelChangeEvent(player, level, calculatedLevel);
            if (EventUtils.callCancellable(levelChangeEvent)) return;

            minetopiaPlayer.setLevel(calculatedLevel);
            return;
        }

        LevelUtil.calculateLevelupCosts(level, calculatedLevel).whenComplete((cost, throwable) -> {
            if (cost == null || throwable != null) return;
            new LevelCheckMenu(minetopiaPlayer, calculatedLevel, cost).open(player);
        });
    }

    private static class LevelCheckMenu extends Menu {
        public LevelCheckMenu(MinetopiaPlayer minetopiaPlayer, int newLevel, double cost) {
            super("<gold>Levelcheck", 3);
            gui.disableAllInteractions();

            BankingModule bankingModule = OpenMinetopia.getModuleManager().get(BankingModule.class);
            if (bankingModule == null) return;

            // Check if the player has enough money
            BankAccountModel bankAccountModel = bankingModule.getAccountById(minetopiaPlayer.getUuid());
            boolean canAfford = bankAccountModel.getBalance() >= cost;

            // Akkoord-knop
            GuiItem acceptItem = new GuiItem(new ItemBuilder(Material.GREEN_WOOL)
                    .setName("<green>Akkoord")
                    .addLoreLine(" ")
                    .addLoreLine("<dark_green>Klik hier om je level te upgraden naar <green>Level " + newLevel + "<dark_green>!")
                    .addLoreLine("<dark_green>Deze upgrade kost <green>" + (cost == 0 ? "niks" : "€" + cost))
                    .toItemStack());
            acceptItem.setAction(event -> {
                if (!canAfford) {
                    ChatUtils.sendFormattedMessage(minetopiaPlayer, MessageConfiguration.message("levelcheck_not_enough_money"));
                    return;
                }

                PlayerLevelChangeEvent levelChangeEvent = new PlayerLevelChangeEvent(minetopiaPlayer.getBukkit().getPlayer(), minetopiaPlayer.getLevel(), newLevel);
                if (EventUtils.callCancellable(levelChangeEvent)) return;

                bankAccountModel.setBalance(bankAccountModel.getBalance() - cost);
                minetopiaPlayer.setLevel(newLevel);
                ChatUtils.sendFormattedMessage(minetopiaPlayer, MessageConfiguration.message("levelcheck_success")
                        .replace("<level>", String.valueOf(newLevel)));
                minetopiaPlayer.save();
                gui.close(event.getWhoClicked());
            });
            gui.setItem(11, acceptItem);

            // Annuleer-knop
            GuiItem cancelItem = new GuiItem(new ItemBuilder(Material.RED_WOOL)
                    .setName("<red>Annuleren")
                    .addLoreLine(" ")
                    .addLoreLine("<dark_red>Klik om deze actie te <red>annuleren</red>.")
                    .toItemStack());
            cancelItem.setAction(event -> gui.close(event.getWhoClicked()));
            gui.setItem(15, cancelItem);
        }
    }
}