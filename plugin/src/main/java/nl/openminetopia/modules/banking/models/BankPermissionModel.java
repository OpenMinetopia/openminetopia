package nl.openminetopia.modules.banking.models;

import com.craftmend.storm.api.StormModel;
import com.craftmend.storm.api.markers.Column;
import com.craftmend.storm.api.markers.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import nl.openminetopia.modules.banking.enums.AccountPermission;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
@RequiredArgsConstructor
@Table(name = "banking_permissions")
public class BankPermissionModel extends StormModel {

    @Column(name = "account")
    private UUID account;

    @Column(name = "uuid")
    private UUID uuid;

    @Column(name = "permission")
    private AccountPermission permission;

}
