package com.example.shared.domain.errors.client;

import com.example.shared.domain.errors.Error;

public class TooManyRequest extends Error {
    private static final String ERROR = "too_many_request";

    public TooManyRequest(String message) {
        super(message, ERROR);
    }

    @Override
    public Integer code() {
        return 429;
    }
}
