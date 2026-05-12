package nl.openminetopia.modules.fitness.objects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nl.openminetopia.api.player.fitness.FitnessStatisticType;
import org.bukkit.Material;

@AllArgsConstructor
@Getter
public class FitnessFood {

    private final FitnessStatisticType fitnessStatisticType;
    private Material material;
    private int customModelData;

}
