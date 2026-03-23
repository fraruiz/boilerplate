package com.example.shared.infrastructure.resilience;

import com.example.shared.domain.resilience.Resilience;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;

import java.util.function.Supplier;

public final class Resilience4jResilience implements Resilience {
    private final CircuitBreakerRegistry circuitBreakerRegistry;
    private final RetryRegistry retryRegistry;

    public Resilience4jResilience(CircuitBreakerRegistry circuitBreakerRegistry, RetryRegistry retryRegistry) {
        this.circuitBreakerRegistry = circuitBreakerRegistry;
        this.retryRegistry = retryRegistry;
    }

    @Override
    public <T> T withCircuitBreaker(String name, Supplier<T> supplier) {
        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker(name);
        return CircuitBreaker.decorateSupplier(circuitBreaker, supplier).get();
    }

    @Override
    public <T> T withRetry(String name, Supplier<T> supplier) {
        Retry retry = retryRegistry.retry(name);
        return Retry.decorateSupplier(retry, supplier).get();
    }
}
