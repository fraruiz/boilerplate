package com.example.app.server.handlers.healthcheck;

import com.example.app.server.handlers.RequestHandler;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import org.eclipse.jetty.http.HttpMethod;

import java.util.Map;

public final class HealthCheckHandler extends RequestHandler {
    @Override
    public void handle(Context context) {
        Map<String, String> response = Map.of("status", "ok");

        super.response(context, HttpStatus.OK, response);
    }

    @Override
    public String path() {
        return "/health-check";
    }

    @Override
    public HttpMethod method() {
        return HttpMethod.GET;
    }
}
