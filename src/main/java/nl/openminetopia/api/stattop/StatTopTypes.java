package nl.openminetopia.api.stattop;

import lombok.experimental.UtilityClass;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

@UtilityClass
public class StatTopTypes {

    private final Map<String, StatTopType> types = new LinkedHashMap<>();

    public void register(StatTopType type) {
        types.put(type.key().toLowerCase(), type);
    }

    public void unregister(StatTopType type) {
        types.remove(type.key().toLowerCase());
    }

    public StatTopType byKey(String key) {
        if (key == null) return null;
        return types.get(key.toLowerCase());
    }

    public Collection<StatTopType> all() {
        return Collections.unmodifiableCollection(types.values());
    }
}
