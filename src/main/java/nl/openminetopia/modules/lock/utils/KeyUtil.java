package nl.openminetopia.modules.lock.utils;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.configuration.DefaultConfiguration;
import nl.openminetopia.utils.ChatUtils;
import nl.openminetopia.utils.PersistentDataUtil;
import nl.openminetopia.utils.WorldGuardUtils;
import nl.openminetopia.utils.item.ItemBuilder;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@UtilityClass
public class KeyUtil {

    private final String KEY = "omt_key";
    private final String MASTER = "omt_key_master";
    private final String LOCKS = "omt_key_locks";
    private final String PLOTS = "omt_key_plots";
    private final String SEPARATOR = "\\|";

    public ItemStack createKey() {
        DefaultConfiguration config = OpenMinetopia.getDefaultConfiguration();

        ItemBuilder builder = new ItemBuilder(config.getKeyMaterial()).setName(config.getKeyName());
        if (config.getKeyCustomModelData() != -1) builder.setCustomModelData(config.getKeyCustomModelData());
        if (config.getKeyItemModel() != null && !config.getKeyItemModel().isEmpty()) builder.setItemModel(config.getKeyItemModel());

        ItemStack item = builder.toItemStack();
        PersistentDataUtil.set(item, "true", KEY);
        updateLore(item);
        return item;
    }

    public boolean isKey(ItemStack item) {
        return item != null && PersistentDataUtil.contains(item, KEY);
    }

    public boolean isMaster(ItemStack key) {
        return "true".equals(PersistentDataUtil.getString(key, MASTER));
    }

    public void setMaster(ItemStack key) {
        PersistentDataUtil.set(key, "true", MASTER);
        updateLore(key);
    }

    public void addLock(ItemStack key, Block block) {
        Set<String> locks = new LinkedHashSet<>(getList(key, LOCKS));
        for (Block linked : LockUtil.getLinkedBlocks(block)) {
            locks.add(serializeBlock(linked));
        }
        setList(key, LOCKS, locks);
        updateLore(key);
    }

    public void addPlot(ItemStack key, String regionId, World world) {
        Set<String> plots = new LinkedHashSet<>(getList(key, PLOTS));
        plots.add(world.getName() + ";" + regionId.toLowerCase());
        setList(key, PLOTS, plots);
        updateLore(key);
    }

    public boolean fits(ItemStack key, Block block) {
        if (!isKey(key)) return false;
        if (isMaster(key)) return true;

        if (getList(key, LOCKS).contains(serializeBlock(block))) return true;

        List<String> plots = getList(key, PLOTS);
        if (plots.isEmpty()) return false;

        for (ProtectedRegion region : WorldGuardUtils.getProtectedRegions(block.getLocation(), p -> p >= 0)) {
            if (plots.contains(block.getWorld().getName() + ";" + region.getId().toLowerCase())) return true;
        }
        return false;
    }

    private String serializeBlock(Block block) {
        return block.getWorld().getName() + ";" + block.getX() + ";" + block.getY() + ";" + block.getZ();
    }

    private List<String> getList(ItemStack item, String key) {
        String raw = PersistentDataUtil.getString(item, key);
        if (raw == null || raw.isEmpty()) return new ArrayList<>();
        return new ArrayList<>(Arrays.asList(raw.split(SEPARATOR)));
    }

    private void setList(ItemStack item, String key, Collection<String> values) {
        PersistentDataUtil.set(item, String.join("|", values), key);
    }

    private void updateLore(ItemStack item) {
        DefaultConfiguration config = OpenMinetopia.getDefaultConfiguration();
        List<Component> lore = new ArrayList<>();

        for (String line : config.getKeyLore()) {
            lore.add(color(line));
        }

        if (isMaster(item)) {
            lore.add(color(config.getKeyLoreMaster()));
        } else {
            List<String> locks = getList(item, LOCKS);
            if (!locks.isEmpty()) {
                lore.add(color(config.getKeyLoreBlocks().replace("<amount>", String.valueOf(locks.size()))));
            }
            for (String plot : getList(item, PLOTS)) {
                String id = plot.contains(";") ? plot.substring(plot.indexOf(";") + 1) : plot;
                lore.add(color(config.getKeyLorePlot().replace("<plot>", id)));
            }
        }

        ItemMeta meta = item.getItemMeta();
        meta.lore(lore);
        item.setItemMeta(meta);
    }

    private Component color(String message) {
        return ChatUtils.color(message).decoration(TextDecoration.ITALIC, false);
    }
}
