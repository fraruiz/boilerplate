package com.example.shared.domain.errors.successful;

import com.example.shared.domain.errors.Error;

public class AlreadyExists extends Error {
    private static final String ERROR = "already_exists";

    public AlreadyExists(String message) {
        super(message, ERROR);
    }

    @Override
    public Integer code() {
        return 208;
    }
}
