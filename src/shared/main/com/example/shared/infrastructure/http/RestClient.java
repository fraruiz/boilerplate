package com.example.shared.infrastructure.http;

import com.example.shared.domain.errors.client.*;
import com.example.shared.domain.errors.internal.GatewayTimeout;
import com.example.shared.domain.errors.internal.InternalError;
import com.example.shared.domain.errors.internal.ServiceUnavailable;
import com.example.shared.domain.logs.Logger;
import com.example.shared.domain.mappers.Mapper;
import com.example.shared.domain.resilience.Resilience;

import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;

public abstract class RestClient {
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String APPLICATION_JSON = "application/json";

    private final Logger logger;
    private final Mapper mapper;
    private final HttpClient httpClient;
    private final String baseUrl;
    private final Resilience resilience;

    protected RestClient(Logger logger, Mapper mapper, Resilience resilience, String baseUrl) {
        this.logger = logger;
        this.mapper = mapper;
        this.resilience = resilience;
        this.baseUrl = baseUrl;
        this.httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();
    }

    protected <T> T get(String path, Class<T> responseType) {
        logger.info("HTTP GET", context(path));
        return execute(requestBuilder(path).GET().build(), responseType);
    }

    protected <T> T post(String path, Object body, Class<T> responseType) {
        logger.info("HTTP POST", context(path));
        return execute(requestBuilder(path)
            .POST(jsonBody(body))
            .header(CONTENT_TYPE, APPLICATION_JSON)
            .build(), responseType);
    }

    protected void post(String path, Object body) {
        logger.info("HTTP POST", context(path));
        execute(requestBuilder(path)
            .POST(jsonBody(body))
            .header(CONTENT_TYPE, APPLICATION_JSON)
            .build(), Void.class);
    }

    protected <T> T put(String path, Object body, Class<T> responseType) {
        logger.info("HTTP PUT", context(path));
        return execute(requestBuilder(path)
            .PUT(jsonBody(body))
            .header(CONTENT_TYPE, APPLICATION_JSON)
            .build(), responseType);
    }

    protected void put(String path, Object body) {
        logger.info("HTTP PUT", context(path));
        execute(requestBuilder(path)
            .PUT(jsonBody(body))
            .header(CONTENT_TYPE, APPLICATION_JSON)
            .build(), Void.class);
    }

    protected <T> T patch(String path, Object body, Class<T> responseType) {
        logger.info("HTTP PATCH", context(path));
        return execute(requestBuilder(path)
            .method("PATCH", jsonBody(body))
            .header(CONTENT_TYPE, APPLICATION_JSON)
            .build(), responseType);
    }

    protected void delete(String path) {
        logger.info("HTTP DELETE", context(path));
        execute(requestBuilder(path).DELETE().build(), Void.class);
    }

    protected Map<String, String> headers() {
        return Map.of();
    }

    private HttpRequest.Builder requestBuilder(String path) {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
            .uri(URI.create(baseUrl + path))
            .timeout(Duration.ofSeconds(30));

        headers().forEach(builder::header);
        return builder;
    }

    private HttpRequest.BodyPublisher jsonBody(Object body) {
        return HttpRequest.BodyPublishers.ofString(mapper.map(body, String.class));
    }

    private <T> T execute(HttpRequest request, Class<T> responseType) {
        String name = getClass().getSimpleName();
        return resilience.withCircuitBreaker(name, () ->
            resilience.withRetry(name, () -> doExecute(request, responseType))
        );
    }

    private <T> T doExecute(HttpRequest request, Class<T> responseType) {
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            logger.info("HTTP response", Map.of(
                "status", response.statusCode(),
                "url", request.uri().toString()
            ));

            if (response.statusCode() >= 400) {
                handleErrorResponse(response);
            }

            if (responseType == Void.class || response.body() == null || response.body().isBlank()) {
                return null;
            }

            return mapper.map(response.body(), responseType);
        } catch (com.example.shared.domain.errors.Error e) {
            throw e;
        } catch (IOException | InterruptedException e) {
            logger.critical("HTTP request failed", context(request.uri().toString()));
            throw new InternalError(e.getMessage(), e);
        }
    }

    private void handleErrorResponse(HttpResponse<String> response) {
        String body = response.body() != null ? response.body() : "";
        switch (response.statusCode()) {
            case 400 -> throw new InvalidArgument(body);
            case 401 -> throw new Unauthorized(body);
            case 403 -> throw new Forbidden(body);
            case 404 -> throw new NotFound(body);
            case 405 -> throw new MethodNotAllowed(body);
            case 406 -> throw new NotAcceptable(body);
            case 408 -> throw new RequestTimeout(body);
            case 409 -> throw new Conflict(body);
            case 423 -> throw new Locked(body);
            case 429 -> throw new TooManyRequest(body);
            case 451 -> throw new UnavailableForLegalReasons(body);
            case 503 -> throw new ServiceUnavailable(body);
            case 504 -> throw new GatewayTimeout(body);
            default  -> throw new InternalError(body);
        }
    }

    private Map<String, Serializable> context(String path) {
        return Map.of("url", baseUrl + path);
    }
}
