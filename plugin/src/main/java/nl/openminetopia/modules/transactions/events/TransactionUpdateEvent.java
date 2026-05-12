package nl.openminetopia.modules.transactions.events;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import nl.openminetopia.modules.banking.models.BankAccountModel;
import nl.openminetopia.modules.transactions.enums.TransactionType;
import nl.openminetopia.utils.events.CustomEvent;
import org.bukkit.event.Cancellable;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor
public class TransactionUpdateEvent extends CustomEvent implements Cancellable {

    private final UUID executorUuid;
    private final String executorUsername;

    private final TransactionType transactionType;
    private final double amount;
    private final BankAccountModel bankAccount;
    private final String description;

    private final long time;

    private boolean cancelled = false;

}
