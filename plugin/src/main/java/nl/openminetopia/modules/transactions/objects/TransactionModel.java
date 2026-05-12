package nl.openminetopia.modules.transactions.objects;

import com.craftmend.storm.api.StormModel;
import com.craftmend.storm.api.markers.Column;
import com.craftmend.storm.api.markers.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import nl.openminetopia.modules.transactions.enums.TransactionType;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
@Table(name = "transaction_logs")
public class TransactionModel extends StormModel {

    @Column(name = "time")
    private Long time;

    @Column(name = "player_uuid")
    private UUID player;

    @Column(name = "player_username")
    private String username;

    @Column(name = "transaction_type")
    private TransactionType type;

    @Column(name = "amount")
    private Double amount;

    @Column(name = "bank_account")
    private UUID bankAccount;

    @Column(name = "description")
    private String description;

}
