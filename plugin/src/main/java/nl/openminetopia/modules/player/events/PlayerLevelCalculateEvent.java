package nl.openminetopia.modules.player.events;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import nl.openminetopia.utils.events.CustomEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

@Data
@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor
public class PlayerLevelCalculateEvent extends CustomEvent implements Cancellable {

    private final Player player;
    private final int level;
    private final int points;

    private boolean cancelled = false;

}
