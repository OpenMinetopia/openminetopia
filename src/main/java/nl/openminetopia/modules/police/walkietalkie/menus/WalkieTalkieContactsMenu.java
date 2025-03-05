package nl.openminetopia.modules.police.walkietalkie.menus;

import com.jazzkuh.inventorylib.objects.PaginatedMenu;
import com.jazzkuh.inventorylib.objects.icon.Icon;
import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.configuration.MessageConfiguration;
import nl.openminetopia.modules.police.PoliceModule;
import nl.openminetopia.utils.ChatUtils;
import nl.openminetopia.utils.item.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

public class WalkieTalkieContactsMenu extends PaginatedMenu {

    private final PoliceModule policeModule = OpenMinetopia.getModuleManager().get(PoliceModule.class);

    public WalkieTalkieContactsMenu(Player player) {
        super(ChatUtils.color("<gold>Contacten"), 3);

        this.registerPageSlotsBetween(0, 17);

        Bukkit.getServer().getOnlinePlayers().forEach(target -> {
            if (target.getName().equals(player.getName())
                    || !target.hasPermission("openminetopia.walkietalkie")
                    || !player.canSee(target) && player.getWorld().getName().equals(target.getWorld().getName())
                    || target.hasPotionEffect(PotionEffectType.INVISIBILITY)) return;

            ItemBuilder contactBuilder = new ItemBuilder(Material.PLAYER_HEAD)
                    .setSkullOwner(target)
                    .setName("<gray>" + target.getName())
                    .addLoreLine("<gray>Stuur een privébericht naar <dark_gray>" + target.getName());

            Icon contactIcon = new Icon(10, contactBuilder.toItemStack(), event -> {
                ChatUtils.sendMessage(player, MessageConfiguration.message("police_walkietalkie_type_your_message")
                        .replace("<player>", target.getName()));
                policeModule.getWalkieTalkieManager().startComposingMessage(player, target);
                this.getInventory().close();
            });
            this.addItem(contactIcon);
        });

        ItemBuilder backItemBuilder = new ItemBuilder(Material.OAK_DOOR)
                .setName("<gray>Terug");

        Icon backIcon = new Icon(22, backItemBuilder.toItemStack(), event -> new WalkieTalkieMenu(player).open(player));
        this.addSpecialIcon(backIcon);
    }

    @Override
    public Icon getPreviousPageItem() {
        return new Icon(18, new ItemBuilder(Material.ARROW)
                .setName("<gold>Vorige pagina")
                .toItemStack(), event -> event.setCancelled(true));
    }

    @Override
    public Icon getNextPageItem() {
        return new Icon(26, new ItemBuilder(Material.ARROW)
                .setName("<gold>Volgende pagina")
                .toItemStack(), event -> event.setCancelled(true));
    }
}
