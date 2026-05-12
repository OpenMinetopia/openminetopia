package nl.openminetopia.modules.staff.admintool.menus.fitness;

import dev.triumphteam.gui.guis.GuiItem;
import lombok.Getter;
import nl.openminetopia.api.player.objects.MinetopiaPlayer;
import nl.openminetopia.modules.banking.models.BankAccountModel;
import nl.openminetopia.modules.fitness.models.FitnessBoosterModel;
import nl.openminetopia.modules.player.utils.PlaytimeUtil;
import nl.openminetopia.utils.item.ItemBuilder;
import nl.openminetopia.utils.menu.PaginatedMenu;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

@Getter
public class AdminToolFitnessBoostersMenu extends PaginatedMenu {

    private final Player player;
    private final OfflinePlayer offlinePlayer;
    private final MinetopiaPlayer minetopiaPlayer;
    private final BankAccountModel bankAccountModel;

    public AdminToolFitnessBoostersMenu(Player player, OfflinePlayer offlinePlayer, MinetopiaPlayer minetopiaPlayer, BankAccountModel bankAccountModel) {
        super("<gold>Fitheid Boosters <yellow>" + offlinePlayer.getPlayerProfile().getName(), 3, 18);
        this.player = player;
        this.offlinePlayer = offlinePlayer;
        this.minetopiaPlayer = minetopiaPlayer;
        this.bankAccountModel = bankAccountModel;

        gui.disableAllInteractions();
        gui.setItem(18, this.previousPageItem());
        gui.setItem(26, this.nextPageItem());

        if (minetopiaPlayer == null) return;

        for (FitnessBoosterModel booster : minetopiaPlayer.getFitness().getBoosters()) {
            ItemBuilder icon = new ItemBuilder(Material.POTION)
                    .setName("<gold>Booster")
                    .addLoreLine(" ")
                    .addLoreLine("<gold>Boost: <yellow>" + booster.getAmount());

            String duration = PlaytimeUtil.formatPlaytime((booster.getExpiresAt() - System.currentTimeMillis()) / 1000);
            if (booster.getExpiresAt() != -1 && booster.getExpiresAt() - System.currentTimeMillis() > -1) icon.addLoreLine("<gold>Deze booster vervalt over <yellow>" + duration + "<gold>.");
            if (booster.isExpired()) icon.addLoreLine("<gold>Deze booster is <red>verlopen<gold>.");
            if (booster.getExpiresAt() == -1) icon.addLoreLine("<gold>Deze booster vervalt <yellow>nooit<gold>.");

            icon.addLoreLine(" ").addLoreLine("<gold>Klik om deze booster te verwijderen.");

            GuiItem boosterItem = new GuiItem(icon.toItemStack(), event -> {
                minetopiaPlayer.getFitness().removeBooster(booster);
                new AdminToolFitnessBoostersMenu(player, offlinePlayer, minetopiaPlayer, bankAccountModel).open((Player) event.getWhoClicked());
            });
            gui.addItem(boosterItem);

            ItemBuilder backItemBuilder = new ItemBuilder(Material.OAK_DOOR)
                    .setName("<gray>Terug");

            GuiItem backItem = new GuiItem(backItemBuilder.toItemStack(), event -> {
                new AdminToolFitnessMenu(player, offlinePlayer, minetopiaPlayer, bankAccountModel).open((Player) event.getWhoClicked());
            });
            gui.setItem(22, backItem);
        }
    }
}
