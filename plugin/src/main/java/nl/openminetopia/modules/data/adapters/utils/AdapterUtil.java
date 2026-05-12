package nl.openminetopia.modules.data.adapters.utils;

import nl.openminetopia.modules.data.adapters.DatabaseAdapter;
import nl.openminetopia.modules.data.adapters.MySQLAdapter;
import nl.openminetopia.modules.data.adapters.SQLiteAdapter;
import nl.openminetopia.modules.data.types.DatabaseType;

public class AdapterUtil {

    public static DatabaseAdapter getAdapter(DatabaseType type) {
        return switch (type) {
            case MYSQL -> new MySQLAdapter();
            case SQLITE -> new SQLiteAdapter();
            //case MONGO -> new MongoAdapter();
            default -> throw new IllegalArgumentException("Unsupported database type: " + type);
        };
    }
}
