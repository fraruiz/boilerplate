package com.example.app.server.handlers.errors;

import io.javalin.http.Context;
import io.javalin.http.ExceptionHandler;

import java.util.Map;

public class KnowErrorHandler implements ExceptionHandler<com.example.shared.domain.errors.Error> {
    @Override
    public void handle(com.example.shared.domain.errors.Error exception, Context ctx) {
        ctx.status(exception.code());
        ctx.json(Map.of("error", exception.error(), "message", exception.getMessage()));
    }
}
