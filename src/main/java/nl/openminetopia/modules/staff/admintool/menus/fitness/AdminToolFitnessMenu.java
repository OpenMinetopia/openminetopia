package nl.openminetopia.modules.staff.admintool.menus.fitness;

import dev.triumphteam.gui.guis.GuiItem;
import lombok.Getter;
import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.api.player.objects.MinetopiaPlayer;
import nl.openminetopia.modules.banking.models.BankAccountModel;
import nl.openminetopia.modules.fitness.FitnessModule;
import nl.openminetopia.modules.fitness.configuration.FitnessConfiguration;
import nl.openminetopia.modules.fitness.models.FitnessStatisticModel;
import nl.openminetopia.modules.staff.admintool.menus.AdminToolInfoMenu;
import nl.openminetopia.utils.item.ItemBuilder;
import nl.openminetopia.utils.menu.PaginatedMenu;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

@Getter
public class AdminToolFitnessMenu extends PaginatedMenu {

    private final Player player;
    private final OfflinePlayer offlinePlayer;
    private final MinetopiaPlayer minetopiaPlayer;
    private final BankAccountModel bankAccountModel;

    public AdminToolFitnessMenu(Player player, OfflinePlayer offlinePlayer, MinetopiaPlayer minetopiaPlayer, BankAccountModel bankAccountModel) {
        super("<gold>Fitheid <yellow>" + offlinePlayer.getPlayerProfile().getName(), 3, 18);
        this.player = player;
        this.offlinePlayer = offlinePlayer;
        this.minetopiaPlayer = minetopiaPlayer;
        this.bankAccountModel = bankAccountModel;

        gui.disableAllInteractions();
        gui.setItem(18, this.previousPageItem());
        gui.setItem(26, this.nextPageItem());

        if (minetopiaPlayer == null) return;

        FitnessConfiguration configuration = OpenMinetopia.getModuleManager().get(FitnessModule.class).getConfiguration();

        ItemBuilder totalItemBuilder = new ItemBuilder(Material.PAPER)
                .setName("<gold>Totaal: <yellow>" + minetopiaPlayer.getFitness().getTotalFitness()
                        + "<gold>/<yellow>" + configuration.getMaxFitnessLevel())
                .addLoreLine(" ")
                .addLoreLine("<gold>Klik om de <yellow>fitness boosters <gold>te bekijken.");

        gui.setItem(20, new GuiItem(totalItemBuilder.toItemStack(), event -> {
            event.setCancelled(true);
            new AdminToolFitnessBoostersMenu(player, offlinePlayer, minetopiaPlayer, bankAccountModel).open((Player) event.getWhoClicked());
        }));

        ItemBuilder backItemBuilder = new ItemBuilder(Material.OAK_DOOR)
                .setName("<gray>Terug");

        gui.setItem(22, new GuiItem(backItemBuilder.toItemStack(), event ->
                new AdminToolInfoMenu(player, offlinePlayer, minetopiaPlayer, bankAccountModel).open((Player) event.getWhoClicked())));

        for (FitnessStatisticModel statistic : minetopiaPlayer.getFitness().getStatistics()) {
            ItemBuilder icon = new ItemBuilder(statistic.getType().icon())
                    .setName("<gold>" + statistic.getType().displayName() + " <yellow>"
                            + statistic.getFitnessGained() + "<gold>/<yellow>" + statistic.getMaximum())
                    .addLoreLine(" ")
                    .addLoreLine("<gold>Voortgang: <yellow>" + statistic.getPoints()
                            + "<gold>/<yellow>" + statistic.getProgressPerPoint());

            gui.addItem(new GuiItem(icon.toItemStack()));
        }
    }
}
