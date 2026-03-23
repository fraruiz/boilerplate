package com.example.app.handlers.filters;

import com.example.shared.domain.logs.RequestContext;
import io.javalin.http.Context;

import java.util.Map;

public final class AfterHandler extends MiddlewareHandler {
    @Override
    public void handle(Context ctx) {
        Long startTime = ctx.attribute("requestStartTime");
        long durationMs = startTime != null ? System.currentTimeMillis() - startTime : -1;

        logger.info("Request completed", Map.of(
            "method", ctx.method().toString(),
            "path", ctx.path(),
            "status", ctx.status().getCode(),
            "durationMs", durationMs
        ));

        RequestContext.clear();
    }
}
