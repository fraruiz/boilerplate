package com.example.shared.domain.errors.internal;

import com.example.shared.domain.errors.Error;

public class InternalError extends Error {
    private static final String ERROR = "internal";

    public InternalError(String message) {
        super(message, ERROR);
    }

    public InternalError(String message, Throwable cause) {
        super(message, cause, ERROR);
    }

    @Override
    public Integer code() {
        return 500;
    }
}
