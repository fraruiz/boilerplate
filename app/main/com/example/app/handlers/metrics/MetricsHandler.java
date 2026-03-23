package com.example.app.handlers.metrics;

import com.example.app.handlers.RequestHandler;
import com.example.shared.infrastructure.ioc.IocContainer;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry;
import org.eclipse.jetty.http.HttpMethod;

public final class MetricsHandler extends RequestHandler {
    private final PrometheusMeterRegistry registry;

    public MetricsHandler() {
        this.registry = IocContainer.getSafeInstance(PrometheusMeterRegistry.class);
    }

    @Override
    public void handle(Context ctx) {
        ctx.contentType("text/plain; version=0.0.4; charset=utf-8");
        ctx.result(registry.scrape());
        ctx.status(HttpStatus.OK);
    }

    @Override
    public String path() {
        return "/metrics";
    }

    @Override
    public HttpMethod method() {
        return HttpMethod.GET;
    }
}
