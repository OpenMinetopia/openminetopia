package nl.openminetopia.modules.fitness.objects;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FitnessLevelEffect {

    private double walkSpeed;
    private List<String> effects;

    public FitnessLevelEffect(double walkSpeed, List<String> effects) {
        this.walkSpeed = walkSpeed;
        this.effects = effects;
    }
}
