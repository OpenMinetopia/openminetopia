package nl.openminetopia.modules.transactions.adapter;

import com.craftmend.storm.Storm;
import com.craftmend.storm.parser.objects.ParsedField;
import com.craftmend.storm.parser.types.objects.StormTypeAdapter;
import nl.openminetopia.modules.transactions.enums.TransactionType;

public class TransactionTypeAdapter extends StormTypeAdapter<TransactionType> {

    @Override
    public TransactionType fromSql(ParsedField parsedField, Object sqlValue) {
        if (sqlValue == null) return null;
        return TransactionType.valueOf(sqlValue.toString());
    }

    @Override
    public Object toSql(Storm storm, TransactionType value) {
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
