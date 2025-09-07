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
import nl.openminetopia.modules.currencies.models.CurrencyModel;
import nl.openminetopia.modules.data.storm.StormDatabase;
import nl.openminetopia.modules.data.storm.adapters.*;
import nl.openminetopia.modules.fitness.models.FitnessBoosterModel;
import nl.openminetopia.modules.fitness.models.FitnessStatisticModel;
import nl.openminetopia.modules.places.models.CityModel;
import nl.openminetopia.modules.places.models.WorldModel;
import nl.openminetopia.modules.player.models.PlayerModel;
import nl.openminetopia.modules.police.models.CriminalRecordModel;
import nl.openminetopia.modules.prefix.models.PrefixModel;
import nl.openminetopia.modules.transactions.adapter.TransactionTypeAdapter;
import nl.openminetopia.modules.transactions.enums.TransactionType;
import nl.openminetopia.modules.transactions.objects.TransactionModel;

public class MySQLAdapter implements DatabaseAdapter {

    @Override
    public void connect() {
        DefaultConfiguration configuration = OpenMinetopia.getDefaultConfiguration();
        String host = configuration.getDatabaseHost();
        int port = configuration.getDatabasePort();
        String name = configuration.getDatabaseName();
        String username = configuration.getDatabaseUsername();
        String password = configuration.getDatabasePassword();

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
        TypeRegistry.registerAdapter(Boolean.class, new FixedBooleanAdapter());
        TypeRegistry.registerAdapter(TransactionType.class, new TransactionTypeAdapter());
        TypeRegistry.registerAdapter(Long.class, new LongTypeAdapter());

        registerStormModel(new BankAccountModel());
        registerStormModel(new BankPermissionModel());
        registerStormModel(new PlayerModel());
        registerStormModel(new FitnessBoosterModel());
        registerStormModel(new FitnessStatisticModel());
        registerStormModel(new PrefixModel());
        registerStormModel(new ColorModel());
        registerStormModel(new WorldModel());
        registerStormModel(new CityModel());
        registerStormModel(new CriminalRecordModel());
        registerStormModel(new TransactionModel());
        registerStormModel(new CurrencyModel());
    }

    @SneakyThrows
    private void registerStormModel(StormModel model) {
        Storm storm = StormDatabase.getInstance().getStorm();

        storm.registerModel(model);
        storm.runMigrations();
    }
}