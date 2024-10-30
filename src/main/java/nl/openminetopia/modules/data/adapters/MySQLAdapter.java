package nl.openminetopia.modules.data.adapters;

import com.craftmend.storm.Storm;
import com.craftmend.storm.api.StormModel;
import com.craftmend.storm.connection.hikaricp.HikariDriver;
import com.craftmend.storm.parser.types.TypeRegistry;
import com.zaxxer.hikari.HikariConfig;
import lombok.SneakyThrows;
import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.api.player.fitness.FitnessStatisticType;
import nl.openminetopia.configuration.DefaultConfiguration;
import nl.openminetopia.modules.banking.enums.AccountPermission;
import nl.openminetopia.modules.banking.enums.AccountType;
import nl.openminetopia.modules.banking.models.BankAccountModel;
import nl.openminetopia.modules.banking.models.BankPermissionModel;
import nl.openminetopia.modules.color.enums.OwnableColorType;
import nl.openminetopia.modules.color.models.ColorModel;
import nl.openminetopia.modules.data.storm.StormDatabase;
import nl.openminetopia.modules.data.storm.adapters.AccountPermissionAdapter;
import nl.openminetopia.modules.data.storm.adapters.AccountTypeAdapter;
import nl.openminetopia.modules.data.storm.adapters.FitnessStatisticTypeAdapter;
import nl.openminetopia.modules.data.storm.adapters.OwnableColorTypeAdapter;
import nl.openminetopia.modules.fitness.models.FitnessBoosterModel;
import nl.openminetopia.modules.fitness.models.FitnessModel;
import nl.openminetopia.modules.fitness.models.FitnessStatisticModel;
import nl.openminetopia.modules.places.models.CityModel;
import nl.openminetopia.modules.places.models.WorldModel;
import nl.openminetopia.modules.player.models.PlayerModel;
import nl.openminetopia.modules.prefix.models.PrefixModel;

public class MySQLAdapter implements DatabaseAdapter{

    public void connect() {
        DefaultConfiguration configuration = OpenMinetopia.getDefaultConfiguration();
        String host = configuration.getHost();
        int port = configuration.getPort();
        String name = configuration.getDatabaseName();
        String username = configuration.getUsername();
        String password = configuration.getPassword();

        try {
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + name);
            config.setUsername(username);
            config.setPassword(password);
            config.setMaximumPoolSize(16);
            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

            StormDatabase.getInstance().setStorm(new Storm(new HikariDriver(config)));
            registerStormModels();
        } catch (Exception e) {
            OpenMinetopia.getInstance().getLogger().severe("Failed to connect to MySQL database: " + e.getMessage());
            OpenMinetopia.getInstance().getLogger().severe("Disabling the plugin...");
            OpenMinetopia.getInstance().getServer().getPluginManager().disablePlugin(OpenMinetopia.getInstance());
        }
    }

    public void registerStormModels() {
        TypeRegistry.registerAdapter(AccountType.class, new AccountTypeAdapter());
        TypeRegistry.registerAdapter(AccountPermission.class, new AccountPermissionAdapter());
        TypeRegistry.registerAdapter(OwnableColorType.class, new OwnableColorTypeAdapter());
        TypeRegistry.registerAdapter(FitnessStatisticType.class, new FitnessStatisticTypeAdapter());

        registerStormModel(new BankAccountModel());
        registerStormModel(new BankPermissionModel());
        registerStormModel(new PlayerModel());
        registerStormModel(new FitnessModel());
        registerStormModel(new FitnessBoosterModel());
        registerStormModel(new FitnessStatisticModel());
        registerStormModel(new PrefixModel());
        registerStormModel(new ColorModel());
        registerStormModel(new WorldModel());
        registerStormModel(new CityModel());
    }

    @SneakyThrows
    private void registerStormModel(StormModel model) {
        Storm storm = StormDatabase.getInstance().getStorm();

        storm.registerModel(model);
        storm.runMigrations();
    }
}