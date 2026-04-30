package nl.openminetopia.modules.backpack.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.modules.backpack.item.BackpackItem;
import nl.openminetopia.modules.items.ItemsModule;
import nl.openminetopia.modules.items.configuration.objects.CustomItem;
import nl.openminetopia.modules.items.configuration.objects.ItemCategory;
import nl.openminetopia.utils.ChatUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

@CommandAlias("rugzak|backpack")
public class BackpackCommand extends BaseCommand {

    @Subcommand("get")
    @CommandPermission("openminetopia.backpack.get")
    @CommandCompletion("@items @range:1-6")
    public void getCommand(Player player, String identifier, @Default("3") int rows) {
        if (rows < 1 || rows > 6) {
            ChatUtils.sendMessage(player, "<red>Het aantal rijen moet tussen 1 en 6 zijn.");
            return;
        }

        ItemsModule itemsModule = OpenMinetopia.getModuleManager().get(ItemsModule.class);

        String[] namespacedKey = identifier.split(":");
        if (namespacedKey.length != 2) {
            ChatUtils.sendMessage(player, "<red>Ongeldig identifier formaat. Gebruik 'namespace:item_name'.");
            return;
        }
        String namespace = namespacedKey[0];
        String itemName = namespacedKey[1];

        Optional<ItemCategory> categoryOpt = itemsModule.getCategoriesConfiguration().category(namespace);
        if (categoryOpt.isEmpty()) {
            ChatUtils.sendMessage(player, "<red>Geen item categorie gevonden met namespace: " + namespace);
            return;
        }
        ItemCategory category = categoryOpt.get();

        Optional<CustomItem> itemOpt = category.item(itemName);
        if (itemOpt.isEmpty()) {
            ChatUtils.sendMessage(player, "<red>Geen item gevonden met identifier: " + identifier);
            return;
        }

        CustomItem source = itemOpt.get();
        ItemStack backpack = BackpackItem.create(source, rows);
        player.getInventory().addItem(backpack);
        ChatUtils.sendMessage(player, "<gold>Je hebt een rugzak ontvangen: " + source.name() + " <gray>(" + rows + " rijen)");
    }
}