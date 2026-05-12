package nl.openminetopia.modules.police.walkietalkie.menus;

import dev.triumphteam.gui.guis.GuiItem;
import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.configuration.DefaultConfiguration;
import nl.openminetopia.configuration.MessageConfiguration;
import nl.openminetopia.modules.police.PoliceModule;
import nl.openminetopia.utils.ChatUtils;
import nl.openminetopia.utils.item.ItemBuilder;
import nl.openminetopia.utils.menu.Menu;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class WalkieTalkieMenu extends Menu {

    public WalkieTalkieMenu(Player player) {
        super("<gold>Portofoon", 3);
        gui.disableAllInteractions();

        PoliceModule policeModule = OpenMinetopia.getModuleManager().get(PoliceModule.class);

        boolean policeChatEnabled = policeModule.getWalkieTalkieManager().isPoliceChatEnabled(player);
        ItemBuilder policeChatBuilder = new ItemBuilder(policeChatEnabled ? Material.GREEN_TERRACOTTA : Material.RED_TERRACOTTA)
                .setName("<gray>Politiechat")
                .addLoreLine(policeChatEnabled ? "<gray>Verlaat de politiechat" : "<gray>Verbind met de politiechat");

        GuiItem policeChatItem = new GuiItem(policeChatBuilder.toItemStack(), event -> {
            policeModule.getWalkieTalkieManager().setPoliceChatEnabled(player, !policeChatEnabled);
            ChatUtils.sendMessage(player, !policeChatEnabled
                    ? MessageConfiguration.message("police_walkietalkie_enabled")
                    : MessageConfiguration.message("police_walkietalkie_disabled"));
            gui.close(player);
        });

        gui.setItem(10, policeChatItem);

        ItemBuilder contactsBuilder = new ItemBuilder(Material.NAME_TAG)
                .setName("<gray>Contacten")
                .addLoreLine("<gray>Stuur een privébericht naar een andere agent");

        GuiItem contactsItem = new GuiItem(contactsBuilder.toItemStack(), event -> {
            new WalkieTalkieContactsMenu(player).open(player);
        });
        gui.setItem(14, contactsItem);

        ItemBuilder emergencyBuilder = new ItemBuilder(Material.RED_WOOL)
                .setName("<red><b>NOODKNOP");

        GuiItem emergencyItem = new GuiItem(emergencyBuilder.toItemStack(), event -> {
            DefaultConfiguration configuration = OpenMinetopia.getDefaultConfiguration();

            if (configuration.isWalkieTalkieEmergencyCooldownEnabled()) {
                if (policeModule.getWalkieTalkieManager().hasCooldown(player)) {
                    ChatUtils.sendMessage(player, MessageConfiguration.message("police_walkietalkie_emergency_cooldown"));
                    return;
                }

                Bukkit.getOnlinePlayers().forEach(onlinePlayer -> {
                    if (!onlinePlayer.hasPermission("openminetopia.walkietalkie")) return;
                    ChatUtils.sendMessage(onlinePlayer, MessageConfiguration.message("police_walkietalkie_emergency_format")
                            .replace("<player>", player.getName())
                            .replace("<world_name>", player.getWorld().getName())
                            .replace("<x>", String.valueOf(Math.round(player.getLocation().x())))
                            .replace("<y>", String.valueOf(Math.round(player.getLocation().y())))
                            .replace("<z>", String.valueOf(Math.round(player.getLocation().z()))));
                });
                policeModule.getWalkieTalkieManager().getEmergencyButtonCooldown().put(player.getUniqueId(),
                        System.currentTimeMillis() + configuration.getWalkieTalkieEmergencyCooldownSeconds());
            }
        });
        gui.setItem(16, emergencyItem);
    }
}
