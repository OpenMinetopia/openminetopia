package nl.openminetopia.modules.places.models;

import com.craftmend.storm.api.StormModel;
import com.craftmend.storm.api.markers.Column;
import com.craftmend.storm.api.markers.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import nl.openminetopia.api.places.objects.MTPlace;

@Data
@EqualsAndHashCode(callSuper=false)
@Table(name = "worlds")
public class WorldModel extends StormModel implements MTPlace {

    @Column(name = "world_name", unique = true)
    private String name;

    @Column(name = "color")
    private String color;

    @Column(name = "title")
    private String title;

    @Column(name = "loading_name")
    private String loadingName;

    @Column(name = "temperature")
    private Double temperature;
}
