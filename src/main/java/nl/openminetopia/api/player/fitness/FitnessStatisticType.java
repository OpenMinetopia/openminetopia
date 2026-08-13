package nl.openminetopia.api.player.fitness;

import org.bukkit.Material;

public interface FitnessStatisticType {

    /**
     * Stable identifier, this is what ends up in the database.
     */
    String key();

    String displayName();

    /**
     * Icon used wherever this statistic is rendered in a menu.
     */
    default Material icon() {
        return Material.PAPER;
    }

    /**
     * The highest amount of fitness this statistic can contribute.
     */
    int maximum();

    /**
     * How much progress is needed for a single fitness point.
     */
    double progressPerPoint();
}
