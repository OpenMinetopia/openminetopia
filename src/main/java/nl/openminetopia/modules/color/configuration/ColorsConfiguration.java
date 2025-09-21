package nl.openminetopia.modules.color.configuration;

import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.modules.color.configuration.components.ColorComponent;
import nl.openminetopia.modules.color.objects.OwnableColor;
import nl.openminetopia.utils.config.ConfigurateConfig;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ColorsConfiguration extends ConfigurateConfig {

    private final Map<String, ColorComponent> components = new HashMap<>();
    private final ConfigurationNode colorsNode;

    public ColorsConfiguration(File file) {
        super(file, "colors.yml", "default/colors.yml", false);

        this.colorsNode = rootNode.node("colors");
        colorsNode.childrenMap().forEach((s, colorNode) -> {
            String identifier = ((String) s).toLowerCase();
            String displayName = colorNode.node("display_name").getString();
            String colorPrefix = colorNode.node("color_prefix").getString();

            components.put(identifier, new ColorComponent(identifier, displayName, colorPrefix));
        });
    }

    public void createColor(String identifier, String displayName, String colorPrefix) {
        components.put(identifier, new ColorComponent(identifier.toLowerCase(), displayName, colorPrefix));

        ConfigurationNode colorNode = colorsNode.node(identifier);
        try {
            colorNode.node("color_prefix").set(colorNode);
            colorNode.node("display_name").set(displayName);
        } catch (SerializationException e) {
            OpenMinetopia.getInstance().getLogger().warning("Serialization went wrong while adding color: " + identifier);
        }

        this.saveConfiguration();
    }

    public List<ColorComponent> lockedColors(List<OwnableColor> ownedColors) {
        List<String> colorStrings = ownedColors.stream().map(OwnableColor::getColorId).map(String::toLowerCase).toList();
        List<ColorComponent> lockedColors = new ArrayList<>();

        components.forEach((identifier, component) -> {
            if (colorStrings.contains(identifier)) return;
            lockedColors.add(component);
        });

        return lockedColors;
    }

    public ColorComponent color(String identifier) {
        return components.get(identifier.toLowerCase());
    }

    public boolean exists(String identifier) {
        return components.containsKey(identifier.toLowerCase());
    }

    public List<ColorComponent> components() {
        return new ArrayList<>(components.values());
    }
}
