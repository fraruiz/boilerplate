package com.example.app.handlers.filters;

import com.example.shared.domain.logs.RequestContext;
import com.example.shared.infrastructure.ioc.IocContainer;
import io.javalin.http.Context;
import io.micrometer.core.instrument.Timer;
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry;

import java.util.Map;

public final class AfterHandler extends MiddlewareHandler {
    private static final String REQUEST_ID_HEADER = "X-Request-Id";
    private final PrometheusMeterRegistry meterRegistry;

    public AfterHandler() {
        this.meterRegistry = IocContainer.getSafeInstance(PrometheusMeterRegistry.class);
    }

    @Override
    public void handle(Context ctx) {
        Long startTime = ctx.attribute("requestStartTime");
        long durationMs = startTime != null ? System.currentTimeMillis() - startTime : -1;

        ctx.header(REQUEST_ID_HEADER, RequestContext.getRequestId());

        recordHttpMetric(ctx);

        logger.info("Request completed", Map.of(
            "status", ctx.status().getCode(),
            "durationMs", durationMs
        ));

        RequestContext.clear();
    }

    private void recordHttpMetric(Context ctx) {
        Timer.Sample sample = ctx.attribute("timerSample");
        if (sample == null) return;

        String uri = ctx.endpoint() != null ? ctx.endpoint().path : ctx.path();

        sample.stop(Timer.builder("http.server.requests")
            .tag("method", ctx.method().toString())
            .tag("uri", uri)
            .tag("status", String.valueOf(ctx.status().getCode()))
            .register(meterRegistry));
    }
}
