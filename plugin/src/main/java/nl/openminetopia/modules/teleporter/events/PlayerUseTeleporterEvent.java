package nl.openminetopia.modules.teleporter.events;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import nl.openminetopia.utils.events.CustomEvent;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

@Data
@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor
public class PlayerUseTeleporterEvent extends CustomEvent implements Cancellable {

    private final Player player;
    private final Location location;

    private boolean cancelled = false;

}
