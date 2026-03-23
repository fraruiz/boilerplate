package com.example.shared.domain.errors.client;

import com.example.shared.domain.errors.Error;

public class RequestTimeout extends Error {
    private static final String ERROR = "timeout";

    public RequestTimeout(String message) {
        super(message, ERROR);
    }

    @Override
    public Integer code() {
        return 408;
    }
}
