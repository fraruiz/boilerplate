package com.example.app.workers;

import com.example.app.workers.handlers.maps.WorkHandlersMapper;
import com.example.shared.domain.properties.PropertiesProvider;
import com.example.shared.infrastructure.ioc.IocContainer;
import io.javalin.Javalin;
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class WorkerApplication {
    public static void execute() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());

        new WorkHandlersMapper().schedule(scheduler);

        startMetricsServer();
    }

    private static void startMetricsServer() {
        PropertiesProvider properties = IocContainer.getSafeInstance(PropertiesProvider.class);
        PrometheusMeterRegistry registry = IocContainer.getSafeInstance(PrometheusMeterRegistry.class);
        int port = Integer.parseInt(properties.execute("worker.port"));

        Javalin.create(config -> config.routes.get("/metrics", ctx -> {
            ctx.contentType("text/plain; version=0.0.4; charset=utf-8");
            ctx.result(registry.scrape());
        })).start(port);
    }
}
