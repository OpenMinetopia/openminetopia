package nl.openminetopia.api.player.fitness;

import lombok.experimental.UtilityClass;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

@UtilityClass
public class FitnessStatisticTypes {

    private final Map<String, FitnessStatisticType> types = new LinkedHashMap<>();

    static {
        for (DefaultFitnessStatisticType type : DefaultFitnessStatisticType.values()) {
            register(type);
        }
    }

    public void register(FitnessStatisticType type) {
        types.put(type.key().toLowerCase(), type);
    }

    public void unregister(FitnessStatisticType type) {
        types.remove(type.key().toLowerCase());
    }

    public void clear() {
        types.clear();
    }

    public FitnessStatisticType byKey(String key) {
        if (key == null) return null;
        return types.get(key.toLowerCase());
    }

    public Collection<FitnessStatisticType> all() {
        return Collections.unmodifiableCollection(types.values());
    }
}
