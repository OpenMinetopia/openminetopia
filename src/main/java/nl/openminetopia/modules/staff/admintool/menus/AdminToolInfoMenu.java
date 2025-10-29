package nl.openminetopia.modules.staff.admintool.menus;

import dev.triumphteam.gui.guis.GuiItem;
import lombok.Getter;
import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.api.player.fitness.Fitness;
import nl.openminetopia.api.player.objects.MinetopiaPlayer;
import nl.openminetopia.modules.banking.BankingModule;
import nl.openminetopia.modules.banking.menus.BankContentsMenu;
import nl.openminetopia.modules.banking.models.BankAccountModel;
import nl.openminetopia.modules.color.enums.OwnableColorType;
import nl.openminetopia.modules.color.menus.ColorTypeMenu;
import nl.openminetopia.modules.fitness.FitnessModule;
import nl.openminetopia.modules.player.events.PlayerLevelChangeEvent;
import nl.openminetopia.modules.player.utils.PlaytimeUtil;
import nl.openminetopia.modules.prefix.menus.PrefixMenu;
import nl.openminetopia.modules.staff.admintool.menus.fitness.AdminToolFitnessMenu;
import nl.openminetopia.utils.ChatUtils;
import nl.openminetopia.utils.events.EventUtils;
import nl.openminetopia.utils.item.ItemBuilder;
import nl.openminetopia.utils.menu.Menu;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

@Getter
public class AdminToolInfoMenu extends Menu {

    private final Player player;
    private final OfflinePlayer offlinePlayer;
    private final MinetopiaPlayer minetopiaPlayer;
    private final BankAccountModel bankAccountModel;

    public AdminToolInfoMenu(Player player, OfflinePlayer offlinePlayer, MinetopiaPlayer minetopiaPlayer, BankAccountModel bankAccountModel) {
        super("<gold>Beheerscherm <yellow>" + offlinePlayer.getPlayerProfile().getName(), 3);
        this.player = player;
        this.offlinePlayer = offlinePlayer;
        this.minetopiaPlayer = minetopiaPlayer;
        this.bankAccountModel = bankAccountModel;

        gui.disableAllInteractions();

        if (minetopiaPlayer == null) {
            player.sendMessage(ChatUtils.color("<red>Er is een fout opgetreden bij het ophalen van de spelergegevens."));
            return;
        }

        ItemBuilder skullBuilder = new ItemBuilder(Material.PLAYER_HEAD)
                .setName("<gold>Profielinformatie")
                .addLoreLine(" ")
                .addLoreLine("<gold>UUID: <yellow>" + offlinePlayer.getUniqueId())
                .addLoreLine("<gold>Naam: " + minetopiaPlayer.getActiveColor(OwnableColorType.NAME).getColorId() + offlinePlayer.getName())
                .addLoreLine("<gold>Prefix: <dark_gray>[" + minetopiaPlayer.getActiveColor(OwnableColorType.PREFIX).getColorId() + minetopiaPlayer.getActivePrefix().getPrefix() + "<dark_gray>]")
                .addLoreLine("<gold>Online tijd: <yellow>" + PlaytimeUtil.formatPlaytime(minetopiaPlayer.getPlaytime()))
                .addLoreLine(" ")
                .setSkullOwner(offlinePlayer);

        GuiItem targetSkullItem = new GuiItem(skullBuilder.toItemStack());
        gui.setItem(10, targetSkullItem);

        /* -------- Prefix -------- */

        ItemBuilder prefixItemBuilder = new ItemBuilder(Material.NAME_TAG)
                .setName("<gold>Prefix")
                .addLoreLine("")
                .addLoreLine("<gold>Klik <yellow>hier <gold>om de <yellow>prefix <gold>van de speler aan te passen.")
                .addLoreLine("");

        GuiItem targetPrefixItem = new GuiItem(prefixItemBuilder.toItemStack(), event -> {
            new PrefixMenu(player, offlinePlayer, minetopiaPlayer).open(player);
        });
        gui.setItem(11, targetPrefixItem);

        /* -------- Colors -------- */

        ItemBuilder colorItemBuilder = new ItemBuilder(Material.YELLOW_CONCRETE)
                .setName("<gold>Kleuren")
                .addLoreLine("")
                .addLoreLine("<gold>Klik <yellow>hier <gold>om de <rainbow>kleuren <gold>van de speler aan te passen.")
                .addLoreLine("");

        GuiItem targetColorItem = new GuiItem(colorItemBuilder.toItemStack(), event -> {
            new ColorTypeMenu(player, offlinePlayer, minetopiaPlayer).open(player);
        });
        gui.setItem(12, targetColorItem);

        /* -------- Level -------- */

        ItemBuilder levelItemBuilder = new ItemBuilder(Material.TRIPWIRE_HOOK)
                .setName("<gold>Level")
                .addLoreLine("<gold>Level: " + minetopiaPlayer.getLevel())
                .addLoreLine("")
                .addLoreLine("<gold>Klik <yellow>hier <gold>om het <yellow>level <gold>van de speler aan te passen.")
                .addLoreLine("")
                .addLoreLine("<yellow>Rechtermuisklik <gold>om het level te verhogen.")
                .addLoreLine("<yellow>Linkermuisklik <gold>om het level te verlagen.");

        GuiItem targetLevelItem = new GuiItem(levelItemBuilder.toItemStack(), event -> {
            PlayerLevelChangeEvent levelChangeEvent = new PlayerLevelChangeEvent(player, minetopiaPlayer.getLevel(), event.isRightClick() ? minetopiaPlayer.getLevel() + 1 : minetopiaPlayer.getLevel() - 1);
            if (EventUtils.callCancellable(levelChangeEvent)) return;

            minetopiaPlayer.setLevel(event.isRightClick() ? minetopiaPlayer.getLevel() + 1 : minetopiaPlayer.getLevel() - 1);
            player.sendMessage(ChatUtils.color("<gold>Je hebt het level van <yellow>" + offlinePlayer.getName() + " <gold>aangepast naar <yellow>" + minetopiaPlayer.getLevel() + "<gold>."));
            new AdminToolInfoMenu(player, offlinePlayer, minetopiaPlayer, bankAccountModel).open(player);
        });
        gui.setItem(13, targetLevelItem);

        /* -------- Fitness -------- */

        FitnessModule fitnessModule = OpenMinetopia.getModuleManager().get(FitnessModule.class);
        if (!OpenMinetopia.getDefaultConfiguration().isModuleDisabled(FitnessModule.class)) {
            Fitness fitness = minetopiaPlayer.getFitness();
            ItemBuilder fitnessItemBuilder = new ItemBuilder(Material.MUSHROOM_STEW)
                    .setName("<gold>Fitheid")
                    .addLoreLine("<gold>Fitheid: " + fitness.getTotalFitness() + " / "
                            + fitnessModule.getConfiguration().getMaxFitnessLevel())
                    .addLoreLine("")
                    .addLoreLine("<gold>Klik <yellow>hier <gold>om de <yellow>fitheid <gold>van de speler te bekijken.");

            GuiItem targetFitnessItem = new GuiItem(fitnessItemBuilder.toItemStack(), event -> {
                new AdminToolFitnessMenu(player, offlinePlayer, minetopiaPlayer, bankAccountModel).open(player);
            });
            gui.setItem(14, targetFitnessItem);
        }

        /* -------- Banking -------- */

        BankingModule bankingModule = OpenMinetopia.getModuleManager().get(BankingModule.class);

        ItemBuilder bankItemBuilder;
        GuiItem targetBankItem;
        if (bankAccountModel != null) {
            bankItemBuilder = new ItemBuilder(Material.GOLD_INGOT)
                    .setName("<gold>Banksaldo")
                    .addLoreLine("<gold>Banksaldo: " + bankingModule.format(bankAccountModel.getBalance()))
                    .addLoreLine("")
                    .addLoreLine("<gold>Klik <yellow>hier <gold>om de <yellow>bank <gold>van de speler te openen.");
            targetBankItem = new GuiItem(bankItemBuilder.toItemStack(), event -> {
                new BankContentsMenu(player, bankAccountModel, true).open(player);
            });
        } else {
            bankItemBuilder = new ItemBuilder(Material.BARRIER)
                    .setName("<gold>Banksaldo")
                    .addLoreLine("<red>Deze speler heeft geen bankrekening.");

            targetBankItem = new GuiItem(bankItemBuilder.toItemStack(), event -> {
                event.setCancelled(true);
                player.sendMessage(ChatUtils.color("<red>Deze speler heeft geen bankrekening."));
            });
        }
        gui.setItem(15, targetBankItem);

        /* -------- Back -------- */

        ItemBuilder backItemBuilder = new ItemBuilder(Material.OAK_DOOR)
                .setName("<gray>Terug");

        GuiItem backItem = new GuiItem(backItemBuilder.toItemStack(), event -> {
            new AdminToolMenu(player, offlinePlayer, minetopiaPlayer, bankAccountModel).open(player);
        });
        gui.setItem(22, backItem);
    }
}
