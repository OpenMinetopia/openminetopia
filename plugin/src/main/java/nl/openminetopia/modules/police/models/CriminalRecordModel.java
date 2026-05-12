package nl.openminetopia.modules.police.models;

import com.craftmend.storm.api.StormModel;
import com.craftmend.storm.api.enums.KeyType;
import com.craftmend.storm.api.markers.Column;
import com.craftmend.storm.api.markers.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import nl.openminetopia.modules.player.models.PlayerModel;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
@Table(name = "criminal_records")
public class CriminalRecordModel extends StormModel {

    @Column(
            keyType = KeyType.FOREIGN,
            references = {PlayerModel.class}
    )
    private Integer playerId;

    @Column(name = "description")
    private String description;

    @Column(name = "officer")
    private UUID officerId;

    @Column(name = "date")
    private Long date;
}
