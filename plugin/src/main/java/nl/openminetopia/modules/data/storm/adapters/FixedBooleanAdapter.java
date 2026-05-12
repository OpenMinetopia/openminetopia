package nl.openminetopia.modules.data.storm.adapters;

import com.craftmend.storm.Storm;
import com.craftmend.storm.parser.objects.ParsedField;
import com.craftmend.storm.parser.types.objects.StormTypeAdapter;

public class FixedBooleanAdapter extends StormTypeAdapter<Boolean> {
    @Override
    public Boolean fromSql(ParsedField parsedField, Object sqlValue) {
        if (sqlValue == null) return null;
        String strVal = sqlValue.toString();
        return strVal.equals("1") || strVal.equals("true");
    }

    @Override
    public Object toSql(Storm storm, Boolean value) {
        return value;
    }

    @Override
    public String getSqlBaseType() {
        return "BOOLEAN";
    }

    @Override
    public boolean escapeAsString() {
        return false;
    }
}

