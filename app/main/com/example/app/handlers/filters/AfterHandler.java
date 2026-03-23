package com.example.app.handlers.filters;

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

        logger.info("Request completed", Map.of(
            "status", ctx.status().getCode(),
            "durationMs", durationMs
        ));

        RequestContext.clear();
    }
}
