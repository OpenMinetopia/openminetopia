package nl.openminetopia.modules.police.walkietalkie.menus;

import dev.triumphteam.gui.guis.GuiItem;
import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.configuration.MessageConfiguration;
import nl.openminetopia.utils.ChatUtils;
import nl.openminetopia.utils.item.ItemBuilder;
import nl.openminetopia.utils.menu.PaginatedMenu;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

public class WalkieTalkieContactsMenu extends PaginatedMenu {

    public WalkieTalkieContactsMenu(Player player) {
        super("<gold>Contacten", 3, 18);

        gui.disableAllInteractions();

        gui.setItem(18, this.previousPageItem());
        gui.setItem(26, this.nextPageItem());

        Bukkit.getServer().getOnlinePlayers().forEach(target -> {
            if (target.getName().equals(player.getName())
                    || !target.hasPermission("openminetopia.walkietalkie")
                    || !player.canSee(target) && player.getWorld().getName().equals(target.getWorld().getName())
                    || target.hasPotionEffect(PotionEffectType.INVISIBILITY)) return;

            ItemBuilder contactBuilder = new ItemBuilder(Material.PLAYER_HEAD)
                    .setSkullOwner(target)
                    .setName("<gray>" + target.getName())
                    .addLoreLine("<gray>Stuur een privébericht naar <dark_gray>" + target.getName());

            GuiItem contactItem = new GuiItem(contactBuilder.toItemStack(), event -> {
                ChatUtils.sendMessage(player, MessageConfiguration.message("police_walkietalkie_type_your_message")
                        .replace("<player>", target.getName()));

                OpenMinetopia.getChatInputHandler().waitForInput(player, response -> {
                    String formattedMessage = MessageConfiguration.message("police_walkietalkie_private_format")
                            .replace("<player>", player.getName())
                            .replace("<target>", target.getName())
                            .replace("<message>", response);

                    ChatUtils.sendMessage(player, formattedMessage);
                    ChatUtils.sendMessage(target, formattedMessage);
                    Bukkit.getConsoleSender().sendMessage(ChatUtils.color(formattedMessage));
                });
                gui.close(player);
            });
            gui.addItem(contactItem);
        });

        ItemBuilder backItemBuilder = new ItemBuilder(Material.OAK_DOOR)
                .setName("<gray>Terug");

        GuiItem backItem = new GuiItem(backItemBuilder.toItemStack(), event -> new WalkieTalkieContactsMenu(player));
        gui.setItem(22, backItem);
    }
}
