package com.example.app.server.handlers.filters;

import com.example.shared.domain.logs.RequestContext;
import io.javalin.http.Context;

import java.util.Map;
import java.util.UUID;

public class BeforeHandler extends MiddlewareHandler {
    private static final String REQUEST_ID_HEADER = "X-Request-Id";

    @Override
    public void handle(Context ctx) {
        String requestId = ctx.header(REQUEST_ID_HEADER);
        if (requestId == null || requestId.isBlank()) {
            requestId = UUID.randomUUID().toString();
        }

        ctx.attribute("requestStartTime", System.currentTimeMillis());

        RequestContext.set(requestId, ctx.method().toString(), ctx.path());

        logger.info("Incoming request", Map.of(
            "queryString", ctx.queryString() != null ? ctx.queryString() : ""
        ));
    }
}
