package com.example.shared.domain.resilience;

import java.util.function.Supplier;

public interface Resilience {
    <T> T withCircuitBreaker(String name, Supplier<T> supplier);
    <T> T withRetry(String name, Supplier<T> supplier);
}
