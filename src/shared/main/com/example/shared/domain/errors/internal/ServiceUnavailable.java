package com.example.shared.domain.errors.internal;

import com.example.shared.domain.errors.Error;

public class ServiceUnavailable extends Error {
    private static final String ERROR = "service_unavailable";

    public ServiceUnavailable(String message) {
        super(message, ERROR);
    }

    @Override
    public Integer code() {
        return 503;
    }
}
