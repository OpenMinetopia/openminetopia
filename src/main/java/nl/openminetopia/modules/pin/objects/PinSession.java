package nl.openminetopia.modules.pin.objects;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import nl.openminetopia.modules.banking.models.BankAccountModel;
import nl.openminetopia.modules.pin.enums.PinState;
import org.bukkit.Location;
import org.bukkit.entity.Player;

@Getter
@Setter
@RequiredArgsConstructor
public class PinSession {

    // - /pin set <username> <amount>

    private final Player executor;
    private final Player target;
    private final double amount;

    private PinState state = PinState.CONSOLE;

    private Location console;
    private BankAccountModel account;

}
