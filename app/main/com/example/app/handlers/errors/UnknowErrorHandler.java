package com.example.app.handlers.errors;

import io.javalin.http.Context;
import io.javalin.http.ExceptionHandler;

import java.util.Map;

public class UnknowErrorHandler implements ExceptionHandler<Exception> {
    @Override
    public void handle(Exception exception, Context ctx) {
        ctx.status(500);
        ctx.json(Map.of("error", "internal", "message", exception.getMessage()));
    }
}
