package nl.openminetopia.modules.backpack.item;

import lombok.experimental.UtilityClass;
import nl.openminetopia.modules.items.configuration.objects.CustomItem;
import nl.openminetopia.utils.item.ItemBuilder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

@UtilityClass
public class BackpackItem {

    public static boolean isBackpack(@Nullable ItemStack stack) {
        if (stack == null) return false;
        ItemMeta meta = stack.getItemMeta();
        if (meta == null) return false;
        return meta.getPersistentDataContainer().has(BackpackKeys.id(), PersistentDataType.STRING);
    }

    @Nullable
    public static UUID getId(ItemStack stack) {
        ItemMeta meta = stack.getItemMeta();
        if (meta == null) return null;
        String raw = meta.getPersistentDataContainer().get(BackpackKeys.id(), PersistentDataType.STRING);
        if (raw == null) return null;
        try {
            return UUID.fromString(raw);
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    public static int getRows(ItemStack stack) {
        ItemMeta meta = stack.getItemMeta();
        if (meta == null) return 0;
        Integer rows = meta.getPersistentDataContainer().get(BackpackKeys.rows(), PersistentDataType.INTEGER);
        return rows == null ? 0 : rows;
    }

    public static ItemStack[] getContents(ItemStack stack, int rows) {
        int size = rows * 9;
        ItemMeta meta = stack.getItemMeta();
        if (meta == null) return new ItemStack[size];

        byte[] bytes = meta.getPersistentDataContainer().get(BackpackKeys.contents(), PersistentDataType.BYTE_ARRAY);
        if (bytes == null || bytes.length == 0) return new ItemStack[size];

        ItemStack[] result = new ItemStack[size];
        try (DataInputStream in = new DataInputStream(new ByteArrayInputStream(bytes))) {
            int length = in.readInt();
            for (int i = 0; i < length; i++) {
                boolean present = in.readBoolean();
                if (!present) continue;
                int byteLen = in.readInt();
                byte[] slotBytes = in.readNBytes(byteLen);
                ItemStack read = ItemStack.deserializeBytes(slotBytes);
                if (i < size) result[i] = read;
            }
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to deserialize backpack contents", ex);
        }
        return result;
    }

    public static void setContents(ItemStack stack, ItemStack[] contents) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (DataOutputStream out = new DataOutputStream(baos)) {
            out.writeInt(contents.length);
            for (ItemStack slot : contents) {
                if (slot == null || slot.isEmpty()) {
                    out.writeBoolean(false);
                    continue;
                }
                byte[] slotBytes = slot.serializeAsBytes();
                out.writeBoolean(true);
                out.writeInt(slotBytes.length);
                out.write(slotBytes);
            }
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to serialize backpack contents", ex);
        }

        ItemMeta meta = stack.getItemMeta();
        meta.getPersistentDataContainer().set(BackpackKeys.contents(), PersistentDataType.BYTE_ARRAY, baos.toByteArray());
        stack.setItemMeta(meta);
    }

    public static UUID assignFreshId(ItemStack stack) {
        UUID fresh = UUID.randomUUID();
        ItemMeta meta = stack.getItemMeta();
        meta.getPersistentDataContainer().set(BackpackKeys.id(), PersistentDataType.STRING, fresh.toString());
        stack.setItemMeta(meta);
        return fresh;
    }

    public static ItemStack create(CustomItem source, int rows) {
        ItemStack stack = source.build();
        UUID id = UUID.randomUUID();

        ItemMeta meta = stack.getItemMeta();
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        pdc.set(BackpackKeys.id(), PersistentDataType.STRING, id.toString());
        pdc.set(BackpackKeys.rows(), PersistentDataType.INTEGER, rows);
        stack.setItemMeta(meta);

        ItemStack[] empty = new ItemStack[rows * 9];
        setContents(stack, empty);

        new ItemBuilder(stack).addLoreLine("<gray>Rugzak (<white>" + rows + " rijen<gray>)");
        return stack;
    }
}
