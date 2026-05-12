package nl.openminetopia.modules.data.storm.adapters;

import com.craftmend.storm.Storm;
import com.craftmend.storm.parser.objects.ParsedField;
import com.craftmend.storm.parser.types.objects.StormTypeAdapter;
import lombok.SneakyThrows;
import nl.openminetopia.api.player.fitness.FitnessStatisticType;

public class FitnessStatisticTypeAdapter extends StormTypeAdapter<FitnessStatisticType> {

    @SneakyThrows
    @Override
    public FitnessStatisticType fromSql(ParsedField parsedField, Object sqlValue) {
        if (sqlValue == null) return null;
        return FitnessStatisticType.valueOf(sqlValue.toString());
    }

    @Override
    public Object toSql(Storm storm, FitnessStatisticType value) {
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
