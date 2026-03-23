package com.example.shared.domain.errors.client;

import com.example.shared.domain.errors.Error;

public class UnavailableForLegalReasons extends Error {
    private static final String ERROR = "unavailable_for_legal_reasons";

    public UnavailableForLegalReasons(String message) {
        super(message, ERROR);
    }

    @Override
    public Integer code() {
        return 451;
    }
}
