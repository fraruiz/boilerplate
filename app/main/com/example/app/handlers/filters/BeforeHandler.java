package com.example.app.handlers.filters;

import com.example.shared.domain.logs.RequestContext;
import com.example.shared.infrastructure.ioc.IocContainer;
import io.javalin.http.Context;
import io.micrometer.core.instrument.Timer;
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry;

import java.util.Map;
import java.util.UUID;

public class BeforeHandler extends MiddlewareHandler {
    private static final String REQUEST_ID_HEADER = "X-Request-Id";
    private final PrometheusMeterRegistry meterRegistry;

    public BeforeHandler() {
        this.meterRegistry = IocContainer.getSafeInstance(PrometheusMeterRegistry.class);
    }

    @Override
    public void handle(Context ctx) {
        String requestId = ctx.header(REQUEST_ID_HEADER);
        if (requestId == null || requestId.isBlank()) {
            requestId = UUID.randomUUID().toString();
        }

        ctx.attribute("requestStartTime", System.currentTimeMillis());
        ctx.attribute("timerSample", Timer.start(meterRegistry));

        RequestContext.set(requestId, ctx.method().toString(), ctx.path());

        logger.info("Incoming request", Map.of(
            "queryString", ctx.queryString() != null ? ctx.queryString() : ""
        ));
    }
}
