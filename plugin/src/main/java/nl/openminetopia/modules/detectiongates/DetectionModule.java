package nl.openminetopia.modules.detectiongates;

import nl.openminetopia.utils.modules.ExtendedSpigotModule;
import com.jazzkuh.modulemanager.spigot.SpigotModuleManager;
import lombok.Getter;
import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.configuration.DefaultConfiguration;
import nl.openminetopia.modules.detectiongates.listeners.DetectionListener;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@Getter
public class DetectionModule extends ExtendedSpigotModule {

    private final Map<Location, Material> blocks = new HashMap<>();

    public DetectionModule(SpigotModuleManager<@NotNull OpenMinetopia> moduleManager) {
        super(moduleManager);
    }

    @Override
    public void onEnable() {
        registerComponent(new DetectionListener());
    }

    @Override
    public void onDisable() {
        blocks.forEach((location, material) -> location.getBlock().setType(material));
    }

    public List<ItemStack> getFlaggedItems(Player player) {
        DefaultConfiguration configuration = OpenMinetopia.getDefaultConfiguration();

        return Arrays.stream(player.getInventory().getContents())
                .filter(Objects::nonNull)
                .filter(item -> !item.getType().isAir())
                .filter(item -> configuration.getDetectionMaterials().stream().anyMatch(flaggedItem -> {
                    if (flaggedItem.getType() != item.getType()) {
                        return false;
                    }
                    ItemMeta itemMeta = item.getItemMeta();
                    ItemMeta flaggedMeta = flaggedItem.getItemMeta();
                    if (flaggedMeta != null && flaggedMeta.hasCustomModelData()) {
                        return itemMeta != null && itemMeta.hasCustomModelData() &&
                                itemMeta.getCustomModelData() == flaggedMeta.getCustomModelData();
                    }
                    return true;
                }))
                .toList();
    }

}
