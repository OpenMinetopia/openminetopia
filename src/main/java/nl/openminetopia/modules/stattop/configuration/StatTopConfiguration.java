package nl.openminetopia.modules.stattop.configuration;

import lombok.Getter;
import nl.openminetopia.api.stattop.StatTopType;
import nl.openminetopia.utils.config.ConfigurateConfig;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class StatTopConfiguration extends ConfigurateConfig {

    private final @Getter int entries;
    private final @Getter int cacheSeconds;
    private final Map<String, Boolean> enabledStats = new HashMap<>();
    private final Map<String, String> displayNames = new HashMap<>();

    public StatTopConfiguration(File file) {
        super(file, "stattop.yml", "default/stattop.yml", true);

        this.entries = rootNode.node("entries").getInt(10);
        this.cacheSeconds = rootNode.node("cache-seconds").getInt(60);

        rootNode.node("stats").childrenMap().forEach((key, value) -> {
            if (!(key instanceof String stat)) return;

            enabledStats.put(stat.toLowerCase(), value.node("enabled").getBoolean(true));

            String displayName = value.node("display-name").getString();
            if (displayName != null) displayNames.put(stat.toLowerCase(), displayName);
        });
    }

    public boolean isEnabled(String key) {
        return enabledStats.getOrDefault(key.toLowerCase(), true);
    }

    public String getDisplayName(StatTopType type) {
        return displayNames.getOrDefault(type.key().toLowerCase(), type.displayName());
    }
}
