package nl.openminetopia.modules.pin;

import com.jazzkuh.modulemanager.spigot.SpigotModule;
import com.jazzkuh.modulemanager.spigot.SpigotModuleManager;
import lombok.Getter;
import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.modules.banking.BankingModule;
import nl.openminetopia.modules.pin.commands.PinSetCommand;
import nl.openminetopia.modules.pin.listeners.PinInteractionListener;
import nl.openminetopia.modules.pin.menu.PinSelectAccountMenu;
import nl.openminetopia.modules.pin.objects.PinSession;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Getter
public class PinModule extends SpigotModule<@NotNull OpenMinetopia> {

    private final List<PinSession> pinSessions = new ArrayList<>();

    public PinModule(SpigotModuleManager<@NotNull OpenMinetopia> moduleManager, BankingModule bankingModule) {
        super(moduleManager);
    }

    @Override
    public void onEnable() {
        registerComponent(new PinSetCommand(this));

        registerComponent(new PinInteractionListener(this));
    }

    public PinSession createPinSession(Player player, Player target, double amount) {
        PinSession session = new PinSession(player, target, amount);
        pinSessions.add(session);
        return session;
    }

    public PinSession getPinSessionFromLocation(Location location) {
        return pinSessions.stream()
                .filter(session -> session.getConsole() == location)
                .findAny()
                .orElse(null);
    }

    public PinSession getPinSessionFromTarget(Player target) {
        return pinSessions.stream()
                .filter(session -> session.getTarget() == target)
                .findAny()
                .orElse(null);
    }


    public PinSession getPinSessionFromPlayer(Player player) {
        return pinSessions.stream()
                .filter(session -> session.getExecutor() == player)
                .findAny()
                .orElse(null);
    }


}
