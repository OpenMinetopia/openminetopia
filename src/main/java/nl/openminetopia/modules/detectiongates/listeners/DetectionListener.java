package nl.openminetopia.modules.detectiongates.listeners;

import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.configuration.DefaultConfiguration;
import nl.openminetopia.modules.detectiongates.DetectionModule;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DetectionListener implements Listener {

    private final List<Block> cooldown = new ArrayList<>();

    @EventHandler
    public void onPressurePlate(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (event.getAction() != Action.PHYSICAL) return;
        DefaultConfiguration configuration = OpenMinetopia.getDefaultConfiguration();
        if(!configuration.isDetectionGateEnabled()) return;
        Block block = event.getClickedBlock();
        if (block == null) return;
        if (block.getType() != configuration.getDetectionPressurePlate()) return;
        if (block.getRelative(BlockFace.DOWN).getType() != configuration.getDetectionActivationBlock()) return;
        if (cooldown.contains(block)) return;

        DetectionModule detectionModule = OpenMinetopia.getModuleManager().get(DetectionModule.class);
        List<ItemStack> flaggedItems = detectionModule.getFlaggedItems(player);

        Map<Material, Material> replacementBlocks = (flaggedItems.isEmpty() ? configuration.getDetectionSafeBlocks() : configuration.getDetectionFlaggedBlocks());

        List<Block> nearbyBlocks = getBlocksInRange(block.getLocation(), 5);
        cooldown.add(block);

        for (Block nearbyBlock : nearbyBlocks) {
            Material replacementBlock = replacementBlocks.get(nearbyBlock.getType());
            if (replacementBlock != null) {
                detectionModule.getBlocks().put(nearbyBlock.getLocation(), nearbyBlock.getType());
                nearbyBlock.setType(replacementBlock);
            }
        }

        Bukkit.getScheduler().runTaskLater(OpenMinetopia.getInstance(), () -> {
            detectionModule.getBlocks().forEach((location, material) -> {
                if (location.getBlock().getType().isAir()) return;
                location.getBlock().setType(material);
            });
            cooldown.remove(block);
        }, 20L * configuration.getDetectionCooldown());

    }

    private List<Block> getBlocksInRange(Location location, int range) {
        List<Block> blocks = new ArrayList<>();
        for (int x = location.getBlockX() - range; x <= location.getBlockX() + range; ++x) {
            for (int y = location.getBlockY() - range; y <= location.getBlockY() + range; ++y) {
                for (int z = location.getBlockZ() - range; z <= location.getBlockZ() + range; ++z) {
                    blocks.add(location.getWorld().getBlockAt(x, y, z));
                }
            }
        }
        return blocks;
    }

}
