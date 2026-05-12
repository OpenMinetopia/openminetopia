package nl.openminetopia.modules.prefix.models;

import com.craftmend.storm.api.StormModel;
import com.craftmend.storm.api.enums.KeyType;
import com.craftmend.storm.api.markers.Column;
import com.craftmend.storm.api.markers.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import nl.openminetopia.modules.player.models.PlayerModel;

@Data
@EqualsAndHashCode(callSuper=false)
@Table(name = "prefixes")
public class PrefixModel extends StormModel {

    @Column(
            keyType = KeyType.FOREIGN,
            references = {PlayerModel.class}
    )
    private Integer playerId;

    @Column(name = "prefix")
    private String prefix;

    @Column(name = "expires_at")
    private Long expiresAt;

    public boolean isExpired() {
        return expiresAt != -1 && System.currentTimeMillis() >= expiresAt;
    }
}
