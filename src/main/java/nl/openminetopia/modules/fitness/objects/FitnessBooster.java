package nl.openminetopia.modules.fitness.objects;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FitnessBooster {

    private int id;
    private int amount;
    private long expiresAt;

    public FitnessBooster(int id, int amount, long expiresAt) {
        this.id = id;
        this.amount = amount;
        this.expiresAt = expiresAt;
    }
}
