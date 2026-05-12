package nl.openminetopia.utils.events;

import lombok.experimental.UtilityClass;
import nl.openminetopia.OpenMinetopia;
import org.bukkit.Bukkit;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;

@UtilityClass
public final class EventUtils {

    /**
     * Calls a cancellable event and ensures it is executed synchronously.
     *
     * @param event the cancellable event to call
     * @return true if the event was cancelled, false otherwise
     */
    public static boolean callCancellable(Cancellable event) {
        // Ensure synchronous execution of the event
        Bukkit.getScheduler().runTask(OpenMinetopia.getInstance(), () -> Bukkit.getPluginManager().callEvent((Event) event));

        return event.isCancelled();
    }

    public static void call(Event event) {
        Bukkit.getPluginManager().callEvent(event);
    }
}
