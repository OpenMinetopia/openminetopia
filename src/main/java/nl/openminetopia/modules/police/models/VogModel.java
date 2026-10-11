package nl.openminetopia.modules.police.models;

import com.craftmend.storm.api.StormModel;
import com.craftmend.storm.api.enums.KeyType;
import com.craftmend.storm.api.markers.Column;
import com.craftmend.storm.api.markers.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import nl.openminetopia.modules.player.models.PlayerModel;

import java.util.UUID;

/**
 * A VOG (verklaring omtrent gedrag) an officer issued: positive when the player had no
 * criminal records at that moment.
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Table(name = "vogs")
public class VogModel extends StormModel {

    @Column(
            keyType = KeyType.FOREIGN,
            references = {PlayerModel.class}
    )
    private Integer playerId;

    @Column(name = "officer")
    private UUID officerId;

    @Column(name = "positive")
    private Boolean positive;

    @Column(name = "record_count")
    private Integer recordCount;

    @Column(name = "date")
    private Long date;

    @Column(name = "valid_until")
    private Long validUntil;
}
