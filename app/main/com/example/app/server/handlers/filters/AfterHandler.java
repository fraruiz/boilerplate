package com.example.app.server.handlers.filters;

import com.example.shared.domain.logs.RequestContext;
import io.javalin.http.Context;

import java.util.Map;

public final class AfterHandler extends MiddlewareHandler {
    private static final String REQUEST_ID_HEADER = "X-Request-Id";

    @Override
    public void handle(Context ctx) {
        Long startTime = ctx.attribute("requestStartTime");
        long durationMs = startTime != null ? System.currentTimeMillis() - startTime : -1;

        ctx.header(REQUEST_ID_HEADER, RequestContext.getRequestId());

        if ("/metrics".equals(ctx.path())) {
            RequestContext.clear();
            return;
        }

        String uri = ctx.endpoint() != null ? ctx.endpoint().path : ctx.path();
        monitoring.incrementCounter("http.server.requests", Map.of(
                "method", ctx.method().toString(),
                "uri", uri,
                "status", String.valueOf(ctx.status().getCode())
                                                                  ));

        logger.info("Request completed", Map.of(
            "status", ctx.status().getCode(),
            "durationMs", durationMs
        ));

        RequestContext.clear();
    }
}
