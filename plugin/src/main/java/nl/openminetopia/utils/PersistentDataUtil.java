package nl.openminetopia.utils;

import lombok.experimental.UtilityClass;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@UtilityClass
public class PersistentDataUtil {

    @NotNull
    public static <T> ItemStack set(ItemStack itemStack, T value, String key) {
        ItemMeta itemMeta = itemStack.getItemMeta();

        PersistentDataContainer data = itemMeta.getPersistentDataContainer();
        NamespacedKey namespacedKey = createNamespacedKey(key);

        PersistentDataType<?, T> dataType = getDataType(value);
        if (dataType == null) {
            throw new IllegalArgumentException("Unsupported type: " + value.getClass().getName());
        }

        data.set(namespacedKey, dataType, value);
        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    public static boolean contains(ItemStack itemStack, String key) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) {
            return false;
        }
        PersistentDataContainer data = itemMeta.getPersistentDataContainer();
        return data.has(createNamespacedKey(key));
    }

    @Nullable
    public static Object get(ItemStack itemStack, String key) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) {
            return null;
        }
        PersistentDataContainer data = itemMeta.getPersistentDataContainer();
        NamespacedKey namespacedKey = createNamespacedKey(key);

        Object result = getData(data, namespacedKey, PersistentDataType.STRING);
        if (result != null) return result;

        result = getData(data, namespacedKey, PersistentDataType.INTEGER);
        if (result != null) return result;

        result = getData(data, namespacedKey, PersistentDataType.BOOLEAN);
        if (result != null) return result;

        result = getData(data, namespacedKey, PersistentDataType.DOUBLE);
        if (result != null) return result;

        result = getData(data, namespacedKey, PersistentDataType.FLOAT);
        if (result != null) return result;

        result = getData(data, namespacedKey, PersistentDataType.LONG);
        if (result != null) return result;

        result = getData(data, namespacedKey, PersistentDataType.BYTE);
        if (result != null) return result;

        result = getData(data, namespacedKey, PersistentDataType.SHORT);
        return result;
    }

    @Nullable
    public static String getString(ItemStack itemStack, String key) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        PersistentDataContainer data = itemMeta.getPersistentDataContainer();
        return getData(data, createNamespacedKey(key), PersistentDataType.STRING);
    }

    @Nullable
    public static Integer getInteger(ItemStack itemStack, String key) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        PersistentDataContainer data = itemMeta.getPersistentDataContainer();
        return getData(data, createNamespacedKey(key), PersistentDataType.INTEGER);
    }

    @Nullable
    public static Boolean getBoolean(ItemStack itemStack, String key) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        PersistentDataContainer data = itemMeta.getPersistentDataContainer();
        return getData(data, createNamespacedKey(key), PersistentDataType.BOOLEAN);
    }

    @Nullable
    public static Double getDouble(ItemStack itemStack, String key) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        PersistentDataContainer data = itemMeta.getPersistentDataContainer();
        return getData(data, createNamespacedKey(key), PersistentDataType.DOUBLE);
    }

    @Nullable
    public static Long getLong(ItemStack itemStack, String key) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        PersistentDataContainer data = itemMeta.getPersistentDataContainer();
        return getData(data, createNamespacedKey(key), PersistentDataType.LONG);
    }

    @NotNull
    private static NamespacedKey createNamespacedKey(String key) {
        return NamespacedKey.minecraft(key);
    }

    @Nullable
    @SuppressWarnings("unchecked")
    private static <T> PersistentDataType<?, T> getDataType(T value) {
        if (value instanceof String) {
            return (PersistentDataType<?, T>) PersistentDataType.STRING;
        } else if (value instanceof Integer) {
            return (PersistentDataType<?, T>) PersistentDataType.INTEGER;
        } else if (value instanceof Boolean) {
            return (PersistentDataType<?, T>) PersistentDataType.BOOLEAN;
        } else if (value instanceof Double) {
            return (PersistentDataType<?, T>) PersistentDataType.DOUBLE;
        } else if (value instanceof Float) {
            return (PersistentDataType<?, T>) PersistentDataType.FLOAT;
        } else if (value instanceof Long) {
            return (PersistentDataType<?, T>) PersistentDataType.LONG;
        } else if (value instanceof Byte) {
            return (PersistentDataType<?, T>) PersistentDataType.BYTE;
        } else if (value instanceof Short) {
            return (PersistentDataType<?, T>) PersistentDataType.SHORT;
        } else {
            return null;
        }
    }

    @Nullable
    private static <T> T getData(PersistentDataContainer data, NamespacedKey key, PersistentDataType<?, T> type) {
        if (data.has(key, type)) {
            return data.get(key, type);
        }
        return null;
    }
}