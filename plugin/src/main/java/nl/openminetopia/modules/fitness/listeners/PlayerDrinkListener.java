package nl.openminetopia.modules.fitness.listeners;

import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.api.player.PlayerManager;
import nl.openminetopia.api.player.fitness.FitnessStatisticType;
import nl.openminetopia.api.player.objects.MinetopiaPlayer;
import nl.openminetopia.configuration.MessageConfiguration;
import nl.openminetopia.modules.fitness.FitnessModule;
import nl.openminetopia.modules.fitness.configuration.FitnessConfiguration;
import nl.openminetopia.modules.fitness.models.FitnessStatisticModel;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;

public class PlayerDrinkListener implements Listener {

    @EventHandler
    public void playerDrink(final PlayerItemConsumeEvent event) {
        ItemStack item = event.getItem();
        if (item.getType() != Material.POTION) return;

        PotionMeta meta = (PotionMeta) item.getItemMeta();
        MinetopiaPlayer minetopiaPlayer = PlayerManager.getInstance().getOnlineMinetopiaPlayer(event.getPlayer());
        if (minetopiaPlayer == null) return;

        FitnessModule fitnessModule = OpenMinetopia.getModuleManager().get(FitnessModule.class);
        FitnessConfiguration configuration = fitnessModule.getConfiguration();

        // check if player drank water less than 5 minutes ago

        // get drinking cooldown from minutes in millis
        long drinkingCooldown = configuration.getDrinkingCooldown() * 60000L;
        if (minetopiaPlayer.getFitness().getLastDrinkingTime() + drinkingCooldown > System.currentTimeMillis()) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(MessageConfiguration.component("fitness_drinking_cooldown"));
            return;
        }

        FitnessStatisticModel drinkingStatistic = minetopiaPlayer.getFitness().getStatistic(FitnessStatisticType.DRINKING);

        double currentDrinkingPoints = drinkingStatistic.getPoints(); // Huidige drink punten
        double drinkingPointsPerBottle = configuration.getDrinkingPointsPerWaterBottle();

        switch (meta.getBasePotionType()) {
            case WATER:
                event.getPlayer().sendMessage(MessageConfiguration.component("fitness_drinking_water"));
                drinkingStatistic.setPoints(currentDrinkingPoints + drinkingPointsPerBottle);
                minetopiaPlayer.getFitness().setLastDrinkingTime(System.currentTimeMillis());
                break;
            case null:
            default:
                double drinkingPointsPerPotion = configuration.getDrinkingPointsPerPotion();
                event.getPlayer().sendMessage(MessageConfiguration.component("fitness_drinking_water"));
                drinkingStatistic.setPoints(currentDrinkingPoints + drinkingPointsPerPotion);
                minetopiaPlayer.getFitness().setLastDrinkingTime(System.currentTimeMillis());
        }

        if (drinkingStatistic.getPoints() >= 1 && drinkingStatistic.getFitnessGained() <= configuration.getMaxFitnessByDrinking()) {
            drinkingStatistic.setFitnessGained(drinkingStatistic.getFitnessGained() + 1);
            drinkingStatistic.setPoints(0.0);
        }

        minetopiaPlayer.getFitness().setStatistic(FitnessStatisticType.DRINKING, drinkingStatistic);
    }
}
