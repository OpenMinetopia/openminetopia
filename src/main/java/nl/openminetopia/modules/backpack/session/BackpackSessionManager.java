package nl.openminetopia.modules.backpack.session;

import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.modules.backpack.item.BackpackItem;
import nl.openminetopia.modules.backpack.menus.BackpackMenu;
import nl.openminetopia.utils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
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

        UUID id = BackpackItem.getId(stack);
        if (id == null) {
            ChatUtils.sendMessage(player, "<red>Deze rugzak is corrupt en kan niet geopend worden.");
            return;
        }

        if (sessionsByBackpack.containsKey(id)) {
            id = BackpackItem.assignFreshId(stack);
        }

        int rows = BackpackItem.getRows(stack);
        if (rows < 1 || rows > 6) {
            ChatUtils.sendMessage(player, "<red>Deze rugzak heeft een ongeldig aantal rijen.");
            return;
        }

        ItemStack[] contents;
        try {
            contents = BackpackItem.getContents(stack, rows);
        } catch (IllegalStateException ex) {
            ChatUtils.sendMessage(player, "<red>Kon de inhoud van deze rugzak niet laden.");
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