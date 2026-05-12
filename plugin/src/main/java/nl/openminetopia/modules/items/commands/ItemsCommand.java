package nl.openminetopia.modules.items.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.modules.items.ItemsModule;
import nl.openminetopia.modules.items.configuration.objects.CustomItem;
import nl.openminetopia.modules.items.configuration.objects.ItemCategory;
import nl.openminetopia.modules.items.menus.ItemCategoriesMenu;
import nl.openminetopia.utils.ChatUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

@CommandAlias("items|ddgitems|mtitems|minetopiaitems")
public class ItemsCommand extends BaseCommand {

    @Default
    @CommandPermission("openminetopia.items")
    public void defaultCommand(Player player) {
        new ItemCategoriesMenu().open(player);
    }

    @Subcommand("get")
    @CommandPermission("openminetopia.items.get")
    @CommandCompletion("@items")
    public void getCommand(Player player, String identifier) {
        ItemsModule module = OpenMinetopia.getModuleManager().get(ItemsModule.class);

        String[] namespacedKey = identifier.split(":");
        if (namespacedKey.length != 2) {
            ChatUtils.sendMessage(player, "<red>Ongeldig identifier formaat. Gebruik 'namespace:item_name'.");
            return;
        }
        String namespace = namespacedKey[0];
        String itemName = namespacedKey[1];

        Optional<ItemCategory> categoryOpt = module.getCategoriesConfiguration().category(namespace);
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
        CustomItem item = itemOpt.get();
        ItemStack itemStack = item.build();
        player.getInventory().addItem(itemStack);
        ChatUtils.sendMessage(player, "<gold>Je hebt het item: " + item.name() + " (" + item.identifier() + ") ontvangen.");
    }
}
