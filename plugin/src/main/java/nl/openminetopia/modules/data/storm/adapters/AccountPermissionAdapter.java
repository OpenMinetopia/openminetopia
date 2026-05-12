package nl.openminetopia.modules.data.storm.adapters;

import com.craftmend.storm.Storm;
import com.craftmend.storm.parser.objects.ParsedField;
import com.craftmend.storm.parser.types.objects.StormTypeAdapter;
import nl.openminetopia.modules.banking.enums.AccountPermission;

public class AccountPermissionAdapter extends StormTypeAdapter<AccountPermission> {

    @Override
    public AccountPermission fromSql(ParsedField parsedField, Object sqlValue) {
        if (sqlValue == null) return null;
        return AccountPermission.valueOf(sqlValue.toString());
    }

    @Override
    public Object toSql(Storm storm, AccountPermission value) {
        if (value == null) return null;
        return value.toString();
    }

    @Override
    public String getSqlBaseType() {
        return "VARCHAR(%max)";
    }

    @Override
    public boolean escapeAsString() {
        return true;
    }

}
