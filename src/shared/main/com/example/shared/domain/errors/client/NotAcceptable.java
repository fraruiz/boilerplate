package com.example.shared.domain.errors.client;

import com.example.shared.domain.errors.Error;

public final class NotAcceptable extends Error {
    private static final String ERROR = "not_acceptable";

    public NotAcceptable(String message) {
        super(message, ERROR);
    }

    @Override
    public Integer code() {
        return 406;
    }
}

