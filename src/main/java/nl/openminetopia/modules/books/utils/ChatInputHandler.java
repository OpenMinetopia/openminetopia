package nl.openminetopia.modules.books.utils;

import io.papermc.paper.event.player.AsyncChatEvent;
import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.api.player.PlayerManager;
import nl.openminetopia.api.player.objects.MinetopiaPlayer;
import nl.openminetopia.modules.books.BooksModule;
import nl.openminetopia.utils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class ChatInputHandler implements Listener {
    private final Map<UUID, Consumer<String>> inputHandlers = new HashMap<>();

    public void waitForInput(Player player, Consumer<String> responseHandler) {
        inputHandlers.put(player.getUniqueId(), responseHandler);
    }

    @EventHandler
    public void playerChat(final AsyncChatEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();

        if (inputHandlers.containsKey(playerId)) {
            event.setCancelled(true);
            Consumer<String> handler = inputHandlers.remove(playerId);
            handler.accept(ChatUtils.rawMiniMessage(event.message()));
        }
    }
}
