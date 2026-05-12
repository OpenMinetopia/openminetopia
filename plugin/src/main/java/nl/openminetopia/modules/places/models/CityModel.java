package nl.openminetopia.modules.places.models;

import com.craftmend.storm.api.StormModel;
import com.craftmend.storm.api.markers.Column;
import com.craftmend.storm.api.markers.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import nl.openminetopia.api.places.objects.MTPlace;

@Data
@EqualsAndHashCode(callSuper=false)
@Table(name = "cities")
public class CityModel extends StormModel implements MTPlace {

    @Column(name = "city_name", unique = true)
    private String name;

    @Column(name = "world_id")
    private Integer worldId;

    @Column(name = "color")
    private String color;

    @Column(name = "title")
    private String title;

    @Column(name = "loading_name")
    private String loadingName;

    @Column(name = "temperature")
    private Double temperature;
}
