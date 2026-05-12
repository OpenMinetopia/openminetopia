package nl.openminetopia.utils.wrappers;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter @Setter
public class CustomNpcClickEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    private boolean cancelled = false;
    private Player clicker;
    private ClickType clickType;
    private final String npcName;

    public CustomNpcClickEvent(Player clicker, ClickType clickType, String npcName) {
        super(!Bukkit.getServer().isPrimaryThread());
        this.clicker = clicker;
        this.clickType = clickType;
        this.npcName = npcName;
    }

    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public enum ClickType {
        LEFT_CLICK,
        RIGHT_CLICK
    }
}
