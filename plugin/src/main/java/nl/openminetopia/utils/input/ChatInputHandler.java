package nl.openminetopia.utils.input;

import io.papermc.paper.event.player.AsyncChatEvent;
import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.utils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class ChatInputHandler implements Listener {

    private record InputSession(Consumer<String> onSuccess, Consumer<String> onFailed, int timeoutTaskId) {
    }

    private final Map<UUID, InputSession> inputSessions = new HashMap<>();

    public void waitForInput(Player player, Consumer<String> onSuccess, Consumer<String> onFailed) {
        UUID playerId = player.getUniqueId();

        // Cancel previous session if it exists
        if (inputSessions.containsKey(playerId)) {
            Bukkit.getScheduler().cancelTask(inputSessions.get(playerId).timeoutTaskId());
        }

        // Schedule timeout
        int taskId = Bukkit.getScheduler().runTaskLater(OpenMinetopia.getInstance(), () -> {
            InputSession session = inputSessions.remove(playerId);
            if (session == null) return;
            ChatUtils.sendMessage(player, "<red>Actie afgebroken, geen invoer ontvangen.");
            if (onFailed != null) onFailed.accept("<red>Actie afgebroken.");
        }, 20L * 60).getTaskId();

        inputSessions.put(playerId, new InputSession(onSuccess, onFailed, taskId));
    }

    public void waitForInput(Player player, Consumer<String> onSuccess) {
        waitForInput(player, onSuccess, null);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void playerChat(final AsyncChatEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();

        InputSession session = inputSessions.remove(playerId);
        if (session != null) {
            event.setCancelled(true);
            Bukkit.getScheduler().cancelTask(session.timeoutTaskId());
            session.onSuccess().accept(ChatUtils.rawMiniMessage(event.message()));
        }
    }
}