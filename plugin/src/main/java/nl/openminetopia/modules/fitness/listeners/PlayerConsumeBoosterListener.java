package nl.openminetopia.modules.fitness.listeners;

import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.api.player.PlayerManager;
import nl.openminetopia.api.player.objects.MinetopiaPlayer;
import nl.openminetopia.modules.fitness.FitnessModule;
import nl.openminetopia.modules.fitness.objects.FitnessItem;
import nl.openminetopia.utils.ChatUtils;
import nl.openminetopia.utils.item.ItemUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class PlayerConsumeBoosterListener implements Listener {

    @EventHandler
    public void playerConsumeBooster(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        addBooster(player, item);
    }

    @EventHandler
    public void playerConsumeBooster(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (event.getItem() == null) return;
        if (event.getItem().getType().isEdible()) return;
        if (event.getHand() == null) return;

        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (addBooster(player, item)) event.setCancelled(true);
    }

    private boolean addBooster(Player player, ItemStack item) {
        FitnessModule fitnessModule = OpenMinetopia.getModuleManager().get(FitnessModule.class);
        for (FitnessItem fitnessItem : fitnessModule.getConfiguration().getFitnessItems().values()) {
            List<ItemStack> fitnessItems = fitnessModule.getConfiguration().getFitnessItems().values().stream()
                    .map(FitnessItem::itemStack)
                    .toList();
            if (!ItemUtils.isSimilarToAny(item, fitnessItems)) continue;

            if (fitnessModule.getFitnessItemCooldowns().containsKey(player.getUniqueId())) {
                long cooldown = fitnessModule.getFitnessItemCooldowns().get(player.getUniqueId());
                if (System.currentTimeMillis() < cooldown) {
                    ChatUtils.sendFormattedMessage(PlayerManager.getInstance().getOnlineMinetopiaPlayer(player), "<red>Je moet nog even wachten voordat je weer een booster kan gebruiken.");
                    return true;
                }
                fitnessModule.getFitnessItemCooldowns().remove(player.getUniqueId());
            }

            MinetopiaPlayer minetopiaPlayer = PlayerManager.getInstance().getOnlineMinetopiaPlayer(player);
            long msTillExpire = System.currentTimeMillis() + (1000L * fitnessItem.fitnessDuration());
            minetopiaPlayer.getFitness().addBooster(fitnessItem.fitnessAmount(), msTillExpire);

            item.setAmount(item.getAmount() - 1);

            long cooldown = System.currentTimeMillis() + (1000L * fitnessItem.cooldown());
            fitnessModule.getFitnessItemCooldowns().put(player.getUniqueId(), cooldown);
            ChatUtils.sendFormattedMessage(minetopiaPlayer, "<gold>Je hebt <yellow>" + fitnessItem.fitnessAmount() + " <gold>fitheid verdiend!");
            return true;
        }
        return false;
    }
}
