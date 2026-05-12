package nl.openminetopia.modules.prefix.events;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import nl.openminetopia.modules.prefix.models.PrefixModel;
import nl.openminetopia.modules.prefix.objects.Prefix;
import nl.openminetopia.utils.events.CustomEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

@Data
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PlayerChangePrefixEvent extends CustomEvent implements Cancellable {

    private final Player player;
    private final Prefix prefix;

    private boolean cancelled = false;

}
