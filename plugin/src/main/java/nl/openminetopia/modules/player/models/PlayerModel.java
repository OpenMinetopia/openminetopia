package nl.openminetopia.modules.player.models;

import com.craftmend.storm.api.StormModel;
import com.craftmend.storm.api.enums.ColumnType;
import com.craftmend.storm.api.markers.Column;
import com.craftmend.storm.api.markers.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import nl.openminetopia.modules.color.models.ColorModel;
import nl.openminetopia.modules.fitness.models.FitnessBoosterModel;
import nl.openminetopia.modules.fitness.models.FitnessStatisticModel;
import nl.openminetopia.modules.police.models.CriminalRecordModel;
import nl.openminetopia.modules.prefix.models.PrefixModel;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper=false)
@Table(name = "players")
public class PlayerModel extends StormModel {

    @Column(name = "uuid", unique = true)
    private UUID uniqueId;

    @Column(name = "level", defaultValue = "1")
    private Integer level;

    @Column(name = "playtime", defaultValue = "0")
    private Long playtime;

    @Column(name = "wage_time", defaultValue = "0")
    private Long wageTime;

    @Column(
            type = ColumnType.ONE_TO_MANY,
            references = PrefixModel.class,
            matchTo = "player_id"
    )
    private List<PrefixModel> prefixes = new ArrayList<>();

    @Column(name = "active_prefix_id")
    private Integer activePrefixId;

    @Column(
            type = ColumnType.ONE_TO_MANY,
            references = ColorModel.class,
            matchTo = "player_id"
    )
    private List<ColorModel> colors = new ArrayList<>();

    @Column(name = "active_prefixcolor_id")
    private Integer activePrefixColorId;

    @Column(name = "active_namecolor_id")
    private Integer activeNameColorId;

    @Column(name = "active_chatcolor_id")
    private Integer activeChatColorId;

    @Column(name = "active_levelcolor_id")
    private Integer activeLevelColorId;

    @Column(name = "staffchat", defaultValue = "false")
    private Boolean staffchatEnabled;

    @Column(name = "command_spy_enabled", defaultValue = "false")
    private Boolean commandSpyEnabled;

    @Column(name = "chat_spy_enabled", defaultValue = "false")
    private Boolean chatSpyEnabled;

    @Column(
            type = ColumnType.ONE_TO_MANY,
            references = FitnessStatisticModel.class,
            matchTo = "player_id"
    )
    private List<FitnessStatisticModel> statistics = new ArrayList<>();

    @Column(
            type = ColumnType.ONE_TO_MANY,
            references = FitnessBoosterModel.class,
            matchTo = "player_id"
    )
    private List<FitnessBoosterModel> boosters = new ArrayList<>();

    @Column(
            type = ColumnType.ONE_TO_MANY,
            references = CriminalRecordModel.class,
            matchTo = "player_id"
    )
    private List<CriminalRecordModel> criminalRecords = new ArrayList<>();

    @Column(name = "fitness_reset", defaultValue = "false")
    private Boolean fitnessReset;
}
