package nl.openminetopia.modules.data.storm.adapters;

import com.craftmend.storm.Storm;
import com.craftmend.storm.parser.objects.ParsedField;
import com.craftmend.storm.parser.types.objects.StormTypeAdapter;

public class LongTypeAdapter extends StormTypeAdapter<Long> {


    @Override
    public Long fromSql(ParsedField parsedField, Object sqlValue) {
        if (sqlValue == null) return null;
        if (sqlValue instanceof Number) {
            return ((Number) sqlValue).longValue();
        }
        try {
            return Long.parseLong(sqlValue.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    public Object toSql(Storm storm, Long value) {
        return value; // Storm verwerkt dit als INTEGER
    }

    @Override
    public String getSqlBaseType() {
        return "INTEGER"; // SQLite gebruikt 64-bit integer = prima voor long
    }

    @Override
    public boolean escapeAsString() {
        return false; // niet als string quoten
    }
}
