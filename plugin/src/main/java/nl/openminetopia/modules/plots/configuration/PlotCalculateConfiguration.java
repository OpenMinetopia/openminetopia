package nl.openminetopia.modules.plots.configuration;

import lombok.Getter;
import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.utils.config.ConfigurateConfig;
import org.bukkit.Material;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@Getter
public class PlotCalculateConfiguration extends ConfigurateConfig {

    private final int buildersWage;
    private final String calculateFormula;
    private final Map<Material, Double> blockValues = new HashMap<>();

    public PlotCalculateConfiguration(File file) {
        super(file, "plotcalculate.yml", "default/plotcalculate.yml", false);

        this.buildersWage = rootNode.node("builders-wage").getInt(20);
        this.calculateFormula = rootNode.node("plot-calculate-formula").getString("<length> * <width> * 32");
        rootNode.node("block-prices").childrenMap().forEach((key, value) -> {
            if (!(key instanceof String materialName)) return;
            Material material = Material.matchMaterial(materialName);
            if (material == null) {
                OpenMinetopia.getInstance().getLogger().warning("Invalid material in plotcalculate.yml: " + key);
                return;
            }
            double valueDouble = value.getDouble(0);
            blockValues.put(material, valueDouble);
        });
    }
}
