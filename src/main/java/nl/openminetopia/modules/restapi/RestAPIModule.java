package nl.openminetopia.modules.restapi;

import com.jazzkuh.modulemanager.spigot.SpigotModule;
import com.jazzkuh.modulemanager.spigot.SpigotModuleManager;
import io.vertx.core.Context;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.modules.data.DataModule;
import nl.openminetopia.modules.restapi.base.VerticleManager;
import nl.openminetopia.modules.restapi.verticles.MainVerticle;
import nl.openminetopia.modules.restapi.verticles.banking.transactions.BankAccountTransactionVerticle;
import nl.openminetopia.modules.restapi.verticles.banking.BankAccountUsersVerticle;
import nl.openminetopia.modules.restapi.verticles.banking.BankAccountVerticle;
import nl.openminetopia.modules.restapi.verticles.banking.BankAccountsVerticle;
import nl.openminetopia.modules.restapi.verticles.banking.transactions.BankAccountTransactionsVerticle;
import nl.openminetopia.modules.restapi.verticles.places.PlacesVerticle;
import nl.openminetopia.modules.restapi.verticles.player.*;
import nl.openminetopia.modules.restapi.verticles.plots.PlotsVerticle;
import nl.openminetopia.utils.FeatureUtils;
import org.jetbrains.annotations.NotNull;

public class RestAPIModule extends SpigotModule<@NotNull OpenMinetopia> {

    public RestAPIModule(SpigotModuleManager<@NotNull OpenMinetopia> moduleManager, DataModule dataModule) {
        super(moduleManager);
    }

    public VerticleManager verticleManager;

    @Override
    public void onEnable() {
        if (OpenMinetopia.getDefaultConfiguration().isRestApiEnabled()) {
            // Check if restapi feature is enabled
            if (FeatureUtils.isFeatureDisabled("restapi")) {
                getLogger().info("REST API feature is disabled in config.yml");
                return;
            }

            Vertx vertx = OpenMinetopia.getInstance().getOrCreateVertx();

            Context context = vertx.getOrCreateContext();

            Router router = Router.router(vertx);
            router.route().handler(BodyHandler.create());

            verticleManager = new VerticleManager(vertx, context, router);
            verticleManager.register(
                    /* GET */
                    new MainVerticle(),
                    new PlayerVerticle(),
                    new PrefixesVerticle(),
                    new ColorsVerticle(),
                    new FitnessStatisticsVerticle(),
                    new BankAccountVerticle(),
                    new BankAccountsVerticle(),
                    new BankAccountUsersVerticle(),
                    new PlayerBankAccountsVerticle(),
                    new CriminalRecordsVerticle(),
                    new PlayerPlotsVerticle(),
                    new PlotsVerticle(),
                    new PlacesVerticle(),

                    /* POST */
                    new BankAccountTransactionsVerticle(),
                    new BankAccountTransactionVerticle()
            );
        }
    }
}