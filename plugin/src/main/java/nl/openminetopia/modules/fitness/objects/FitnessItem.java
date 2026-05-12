package nl.openminetopia.modules.fitness.objects;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.bukkit.inventory.ItemStack;

@Getter
@Accessors(fluent = true)
public class FitnessItem {
    private final String identifier;
    private final ItemStack itemStack;
    private final int fitnessAmount;
    private final int fitnessDuration;
    private final int cooldown;

    public FitnessItem(String identifier, ItemStack itemStack, int fitnessAmount, int fitnessDuration, int cooldown) {
        this.identifier = identifier;
        this.itemStack = itemStack;
        this.fitnessAmount = fitnessAmount;
        this.fitnessDuration = fitnessDuration;
        this.cooldown = cooldown;
    }
}
