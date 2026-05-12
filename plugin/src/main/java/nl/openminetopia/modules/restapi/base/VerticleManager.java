package nl.openminetopia.modules.restapi.base;

import io.vertx.core.Context;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import lombok.SneakyThrows;

import java.util.LinkedHashMap;
import java.util.Map;

public class VerticleManager {

    private final Vertx vertx;
    private final Context context;
    private final Router router;

    public VerticleManager(Vertx vertx, Context context, Router router) {
        this.vertx = vertx;
        this.context = context;
        this.router = router;
    }

    private final Map<Class<? extends BaseVerticle>, BaseVerticle> verticles = new LinkedHashMap<>();

    @SneakyThrows
    public void register(BaseVerticle... verticles) {
        for (BaseVerticle verticle : verticles) {
            this.verticles.put(verticle.getClass(), verticle);
            verticle.init(this.vertx, this.context, this.router);
            verticle.start(Promise.promise());
        }
    }

    public <M> M getVerticle(Class<M> clazz) {
        return clazz.cast(verticles.get(clazz));
    }
}
