package com.example.app.handlers.filters;

import com.example.shared.domain.logs.RequestContext;
import io.javalin.http.Context;

import java.util.Map;
import java.util.UUID;

public class BeforeHandler extends MiddlewareHandler {
    @Override
    public void handle(Context ctx) {
        String requestId = UUID.randomUUID().toString();
        ctx.attribute("requestStartTime", System.currentTimeMillis());

        RequestContext.setRequestId(requestId);

        logger.info("Incoming request", Map.of(
            "method", ctx.method().toString(),
            "path", ctx.path(),
            "queryString", ctx.queryString() != null ? ctx.queryString() : "",
            "headers", mapper.map(ctx.headerMap(), String.class)
        ));
    }
}
