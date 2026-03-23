package com.example.shared.domain.errors.client;

import com.example.shared.domain.errors.Error;

public class Locked extends Error {
    private static final String ERROR = "locked";

    public Locked(String message) {
        super(message, ERROR);
    }

    @Override
    public Integer code() {
        return 423;
    }
}
