package nl.openminetopia.modules.staff.admintool.menus;

import dev.triumphteam.gui.guis.GuiItem;
import lombok.Getter;
import nl.openminetopia.api.player.objects.MinetopiaPlayer;
import nl.openminetopia.modules.banking.models.BankAccountModel;
import nl.openminetopia.utils.ChatUtils;
import nl.openminetopia.utils.item.ItemBuilder;
import nl.openminetopia.utils.menu.Menu;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

@Getter
public class AdminToolMenu extends Menu {

    private final OfflinePlayer offlinePlayer;
    private final Player player;
    private final MinetopiaPlayer minetopiaPlayer;
    private final BankAccountModel bankAccountModel;

    public AdminToolMenu(Player player, OfflinePlayer offlinePlayer, MinetopiaPlayer minetopiaPlayer, BankAccountModel bankAccountModel) {
        super("<gold>Beheerscherm <yellow>" + offlinePlayer.getPlayerProfile().getName(), 3);
        this.player = player;
        this.offlinePlayer = offlinePlayer;
        this.minetopiaPlayer = minetopiaPlayer;
        this.bankAccountModel = bankAccountModel;

        gui.disableAllInteractions();

        ItemBuilder skullBuilder = new ItemBuilder(Material.PLAYER_HEAD)
                .setName("<gold>Minetopia Informatie")
                .addLoreLine("")
                .addLoreLine("<gold>Klik <yellow>hier <gold>om de <yellow>MT-Info <gold>te openen.")
                .addLoreLine("")
                .setSkullOwner(offlinePlayer);

        GuiItem targetSkullItem = new GuiItem(skullBuilder.toItemStack(), event -> {
            new AdminToolInfoMenu(player, offlinePlayer, minetopiaPlayer, bankAccountModel).open((Player) event.getWhoClicked());
        });
        gui.setItem(10, targetSkullItem);

        ItemBuilder enderchestBuilder = new ItemBuilder(Material.ENDER_CHEST)
                .setName("<gold>Enderchest")
                .addLoreLine("")
                .addLoreLine("<gold>Klik <yellow>hier <gold>om de <yellow>enderchest <gold>te openen.")
                .addLoreLine("");

        GuiItem enderchestItem = new GuiItem(enderchestBuilder.toItemStack(), event -> {
            Player targetPlayer = offlinePlayer.getPlayer();
            if (targetPlayer == null) return;
            player.sendMessage(ChatUtils.color("<dark_green>Je opent de enderchest van <green>" + targetPlayer.getName() + "<dark_green>."));
            player.openInventory(targetPlayer.getEnderChest());
        });
        if (offlinePlayer.isOnline()) gui.setItem(16, enderchestItem);
    }
}
