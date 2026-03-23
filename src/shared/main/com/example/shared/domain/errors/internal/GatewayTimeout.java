package com.example.shared.domain.errors.internal;

import com.example.shared.domain.errors.Error;

public class GatewayTimeout extends Error {
    private static final String ERROR = "timeout";

    public GatewayTimeout(String message) {
        super(message, ERROR);
    }

    @Override
    public Integer code() {
        return 504;
    }
}
