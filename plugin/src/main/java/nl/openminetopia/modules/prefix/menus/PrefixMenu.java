package nl.openminetopia.modules.prefix.menus;

import dev.triumphteam.gui.guis.GuiItem;
import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.api.player.objects.MinetopiaPlayer;
import nl.openminetopia.configuration.MessageConfiguration;
import nl.openminetopia.modules.prefix.events.PlayerChangePrefixEvent;
import nl.openminetopia.modules.prefix.objects.Prefix;
import nl.openminetopia.utils.ChatUtils;
import nl.openminetopia.utils.events.EventUtils;
import nl.openminetopia.utils.item.ItemBuilder;
import nl.openminetopia.utils.menu.PaginatedMenu;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class PrefixMenu extends PaginatedMenu {

    public PrefixMenu(Player player, OfflinePlayer offlinePlayer, MinetopiaPlayer minetopiaPlayer) {
        super("<black>Kies een prefix", 2, 9);
        gui.disableAllInteractions();

        if (minetopiaPlayer == null) return;

        List<Prefix> prefixes = new ArrayList<>(minetopiaPlayer.getPrefixes());

        if (minetopiaPlayer.getActivePrefix() != null && minetopiaPlayer.getActivePrefix().getId() != -1) {
            prefixes.add(new Prefix(-1, OpenMinetopia.getDefaultConfiguration().getDefaultPrefix(), -1));
        }

        prefixes.removeIf(prefix -> prefix.getId() == minetopiaPlayer.getActivePrefix().getId());

        gui.setItem(12, this.previousPageItem());
        gui.setItem(14, this.nextPageItem());

        GuiItem selectedPrefixItem = new GuiItem(new ItemBuilder(Material.NAME_TAG)
                .setName("<white>" + minetopiaPlayer.getActivePrefix().getPrefix())
                .addLoreLine("")
                .addLoreLine("<gold>Je hebt deze prefix geselecteerd.")
                .setGlowing(true)
                .toItemStack(),
                event -> event.setCancelled(true));
        gui.addItem(selectedPrefixItem);

        for (Prefix prefix : prefixes) {
            var builder = new ItemBuilder(Material.PAPER)
                    .setName("<white>" + prefix.getPrefix())
                    .addLoreLine("")
                    .addLoreLine("<gold>Klik <yellow>hier <gold>om deze prefix te selecteren.")
                    .addLoreLine("");

            if (prefix.getExpiresAt() != -1 && prefix.getExpiresAt() - System.currentTimeMillis() < -1)
                builder.addLoreLine("<red>Deze prefix is vervallen.");
            if (prefix.getExpiresAt() != -1 && prefix.getExpiresAt() - System.currentTimeMillis() > -1)
                builder.addLoreLine("<gold>Deze prefix vervalt over <yellow>" + millisToTime(prefix.getExpiresAt() - System.currentTimeMillis()) + "<gold>.");
            if (prefix.getExpiresAt() == -1) builder.addLoreLine("<gold>Deze prefix vervalt <yellow>nooit<gold>.");

            GuiItem prefixItem = new GuiItem(builder.toItemStack(),
                    event -> {
                        event.setCancelled(true);

                        Prefix toSet = prefix.getId() == -1 ? new Prefix(-1, OpenMinetopia.getDefaultConfiguration().getDefaultPrefix(), -1) : prefix;

                        PlayerChangePrefixEvent changePrefixEvent = new PlayerChangePrefixEvent(player, toSet);
                        if (EventUtils.callCancellable(changePrefixEvent)) return;

                        minetopiaPlayer.setActivePrefix(toSet);
                        player.sendMessage(ChatUtils.format(minetopiaPlayer, "<gold>Je hebt de prefix <yellow>" + prefix.getPrefix() + " <gold>geselecteerd."));
                        new PrefixMenu(player, offlinePlayer, minetopiaPlayer).open(player);
                    });
            gui.addItem(prefixItem);
        }
    }

    private String millisToTime(long millis) {
        long totalSeconds = millis / 1000;
        long totalMinutes = totalSeconds / 60;
        long totalHours = totalMinutes / 60;

        long days = totalHours / 24;
        long hours = totalHours % 24;
        long minutes = totalMinutes % 60;
        long seconds = totalSeconds % 60;

        return MessageConfiguration.message("time_format")
                .replace("<days>", String.valueOf(days))
                .replace("<hours>", String.valueOf(hours))
                .replace("<minutes>", String.valueOf(minutes))
                .replace("<seconds>", String.valueOf(seconds));
    }
}
