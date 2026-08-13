package nl.openminetopia.modules.fitness.models;

import com.craftmend.storm.api.StormModel;
import com.craftmend.storm.api.enums.KeyType;
import com.craftmend.storm.api.markers.Column;
import com.craftmend.storm.api.markers.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import nl.openminetopia.api.player.fitness.FitnessStatisticType;
import nl.openminetopia.modules.player.models.PlayerModel;

@Data
@EqualsAndHashCode(callSuper=false)
@Table(name = "fitness_statistics")
public class FitnessStatisticModel extends StormModel {

    @Column(
            keyType = KeyType.FOREIGN,
            references = {PlayerModel.class}
    )
    private Integer playerId;

    @Column(name = "type")
    private FitnessStatisticType type;

    @Column(name = "fitness_gained", defaultValue = "0")
    private Integer fitnessGained;

    /**
     * For the food statistic, this is the calculated points based on cheap and expensive food
     */
    @Column(name = "points", defaultValue = "0")
    private Double points;

    /**
     * For the food statistic, this is the amount of cheap food eaten
     */
    @Column(name = "secondary_points", defaultValue = "0")
    private Double secondaryPoints;

    /**
     * For the food statistic, this is the amount of expensive food eaten
     */
    @Column(name = "tertiary_points", defaultValue = "0")
    private Double tertiaryPoints;

    public int getMaximum() {
        return type.maximum();
    }

    public double getProgressPerPoint() {
        return type.progressPerPoint();
    }
}
