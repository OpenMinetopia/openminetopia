package nl.openminetopia.modules.currencies.models;

import com.craftmend.storm.api.StormModel;
import com.craftmend.storm.api.markers.Column;
import com.craftmend.storm.api.markers.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.modules.currencies.CurrencyModule;
import nl.openminetopia.modules.currencies.objects.RegisteredCurrency;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "currencies")
public class CurrencyModel extends StormModel {

    @Column(name = "uuid")
    private UUID uniqueId;

    @Column(name = "name")
    private String name;

    @Column(name = "amount", defaultValue = "0")
    private Double balance;

    @Column(name = "last_reward", defaultValue = "0")
    private Long lastReward;

    public RegisteredCurrency configModel() {
        CurrencyModule currencyModule = OpenMinetopia.getModuleManager().get(CurrencyModule.class);
        return currencyModule.getCurrencies().stream()
                .filter(model -> model.getId().equalsIgnoreCase(name))
                .findAny()
                .orElse(null);
    }
}
