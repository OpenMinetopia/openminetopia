package nl.openminetopia.modules.backpack.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.configuration.MessageConfiguration;
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
            ChatUtils.sendMessage(player, MessageConfiguration.message("backpack_invalid_rows"));
            return;
        }

        ItemsModule itemsModule = OpenMinetopia.getModuleManager().get(ItemsModule.class);

        String[] namespacedKey = identifier.split(":");
        if (namespacedKey.length != 2) {
            ChatUtils.sendMessage(player, MessageConfiguration.message("backpack_invalid_identifier"));
            return;
        }
        String namespace = namespacedKey[0];
        String itemName = namespacedKey[1];

        Optional<ItemCategory> categoryOpt = itemsModule.getCategoriesConfiguration().category(namespace);
        if (categoryOpt.isEmpty()) {
            ChatUtils.sendMessage(player, MessageConfiguration.message("backpack_category_not_found")
                    .replace("<namespace>", namespace));
            return;
        }
        ItemCategory category = categoryOpt.get();

        Optional<CustomItem> itemOpt = category.item(itemName);
        if (itemOpt.isEmpty()) {
            ChatUtils.sendMessage(player, MessageConfiguration.message("backpack_item_not_found")
                    .replace("<identifier>", identifier));
            return;
        }

        CustomItem source = itemOpt.get();
        ItemStack backpack = BackpackItem.create(source, rows);
        player.getInventory().addItem(backpack);
        ChatUtils.sendMessage(player, MessageConfiguration.message("backpack_received")
                .replace("<name>", source.name())
                .replace("<rows>", String.valueOf(rows)));
    }

    @Subcommand("make")
    @CommandPermission("openminetopia.backpack.make")
    @CommandCompletion("@range:1-6")
    public void makeCommand(Player player, @Default("3") int rows) {
        if (rows < 1 || rows > 6) {
            ChatUtils.sendMessage(player, MessageConfiguration.message("backpack_invalid_rows"));
            return;
        }

        ItemStack inHand = player.getInventory().getItemInMainHand();
        if (inHand.isEmpty()) {
            ChatUtils.sendMessage(player, MessageConfiguration.message("backpack_no_item_in_hand"));
            return;
        }

        if (BackpackItem.isBackpack(inHand)) {
            ChatUtils.sendMessage(player, MessageConfiguration.message("backpack_already_backpack"));
            return;
        }

        if (inHand.getAmount() > 1) {
            ChatUtils.sendMessage(player, MessageConfiguration.message("backpack_must_be_single"));
            return;
        }

        BackpackItem.markAsBackpack(inHand, rows);
        ChatUtils.sendMessage(player, MessageConfiguration.message("backpack_made")
                .replace("<rows>", String.valueOf(rows)));
    }

    @Subcommand("title")
    @CommandPermission("openminetopia.backpack.title")
    public void titleCommand(Player player, String title) {
        ItemStack inHand = player.getInventory().getItemInMainHand();
        if (!BackpackItem.isBackpack(inHand)) {
            ChatUtils.sendMessage(player, MessageConfiguration.message("backpack_not_a_backpack"));
            return;
        }

        BackpackItem.setTitle(inHand, title);
        ChatUtils.sendMessage(player, MessageConfiguration.message("backpack_title_set")
                .replace("<title>", title));
    }

    @Subcommand("resettitle")
    @CommandPermission("openminetopia.backpack.title")
    public void resetTitleCommand(Player player) {
        ItemStack inHand = player.getInventory().getItemInMainHand();
        if (!BackpackItem.isBackpack(inHand)) {
            ChatUtils.sendMessage(player, MessageConfiguration.message("backpack_not_a_backpack"));
            return;
        }

        BackpackItem.clearTitle(inHand);
        ChatUtils.sendMessage(player, MessageConfiguration.message("backpack_title_cleared"));
    }
}
