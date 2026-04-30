package nl.openminetopia.modules.backpack.session;

import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.configuration.MessageConfiguration;
import nl.openminetopia.modules.backpack.item.BackpackItem;
import nl.openminetopia.modules.backpack.menus.BackpackMenu;
import nl.openminetopia.utils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BackpackSessionManager {

    private final Map<UUID, BackpackSession> sessionsByBackpack = new HashMap<>();
    private final Map<UUID, BackpackSession> sessionsByViewer = new HashMap<>();

    public void open(Player player, ItemStack stack) {
        if (!BackpackItem.isBackpack(stack)) return;

        if (sessionsByViewer.containsKey(player.getUniqueId())) return;

        int rows = BackpackItem.getRows(stack);
        if (rows < 1 || rows > 6) {
            ChatUtils.sendMessage(player, MessageConfiguration.message("backpack_invalid_row_count"));
            return;
        }

        UUID id = BackpackItem.getId(stack);
        boolean freshlyMinted = false;

        if (id == null) {
            // First time this template backpack is being opened — mint its identity now.
            id = BackpackItem.assignFreshId(stack);
            freshlyMinted = true;
        } else if (sessionsByBackpack.containsKey(id)) {
            // Another player already has a backpack with this id open. The held stack
            // is a duplicate (e.g. a creative copy) — give it its own identity.
            id = BackpackItem.assignFreshId(stack);
            freshlyMinted = true;
        }

        if (!freshlyMinted) {
            int removed = removeInventoryDuplicates(player, id);
            if (removed > 0) {
                ChatUtils.sendMessage(player, MessageConfiguration.message("backpack_duplicates_removed")
                        .replace("<count>", String.valueOf(removed)));
            }
        }

        ItemStack[] contents;
        try {
            contents = BackpackItem.getContents(stack, rows);
        } catch (IllegalStateException ex) {
            ChatUtils.sendMessage(player, MessageConfiguration.message("backpack_load_failed"));
            OpenMinetopia.getInstance().getLogger().warning("Failed to deserialize backpack " + id + ": " + ex.getMessage());
            return;
        }

        BackpackSession session = new BackpackSession(player.getUniqueId(), id, stack, rows);
        BackpackMenu menu = new BackpackMenu(this, session, contents);
        session.attachMenu(menu);

        sessionsByBackpack.put(id, session);
        sessionsByViewer.put(player.getUniqueId(), session);

        menu.open(player);
    }

    public void saveSnapshot(BackpackSession session) {
        if (session.getMenu() == null) return;
        ItemStack[] live = session.getMenu().gui.getInventory().getContents();
        ItemStack[] snapshot = new ItemStack[session.getRows() * 9];
        int copy = Math.min(live.length, snapshot.length);
        System.arraycopy(live, 0, snapshot, 0, copy);
        BackpackItem.setContents(session.getStackRef(), snapshot);
    }

    public void close(BackpackSession session) {
        sessionsByBackpack.remove(session.getBackpackId());
        sessionsByViewer.remove(session.getViewer());
    }

    @Nullable
    public BackpackSession getSession(UUID backpackId) {
        return sessionsByBackpack.get(backpackId);
    }

    @Nullable
    public BackpackSession getSessionByViewer(UUID viewer) {
        return sessionsByViewer.get(viewer);
    }

    public boolean isOpen(UUID backpackId) {
        return sessionsByBackpack.containsKey(backpackId);
    }

    public void scheduleSave(BackpackSession session) {
        Bukkit.getScheduler().runTask(OpenMinetopia.getInstance(), () -> {
            if (sessionsByBackpack.get(session.getBackpackId()) == session) {
                saveSnapshot(session);
            }
        });
    }

    private int removeInventoryDuplicates(Player player, UUID id) {
        PlayerInventory inv = player.getInventory();
        int heldSlot = inv.getHeldItemSlot();
        int removed = 0;

        for (int i = 0; i < 36; i++) {
            if (i == heldSlot) continue;
            ItemStack item = inv.getItem(i);
            if (item == null) continue;
            if (id.equals(BackpackItem.getId(item))) {
                inv.setItem(i, null);
                removed++;
            }
        }

        ItemStack offhand = inv.getItemInOffHand();
        if (id.equals(BackpackItem.getId(offhand))) {
            inv.setItemInOffHand(null);
            removed++;
        }

        return removed;
    }

    public void closeAll() {
        for (BackpackSession session : new HashMap<>(sessionsByBackpack).values()) {
            saveSnapshot(session);
            Player viewer = Bukkit.getPlayer(session.getViewer());
            if (viewer != null) viewer.closeInventory();
        }
        sessionsByBackpack.clear();
        sessionsByViewer.clear();
    }
}