package com.example.shared.domain.errors;

import java.util.Objects;

public abstract class Error extends RuntimeException {
    private final String error;

    public Error(String message, String error) {
        super(message);
        this.error = error;
    }

    public Error(String message, Throwable cause, String error) {
        super(message, cause);
        this.error = error;
    }

    public String error() {
        return error;
    }

    public abstract Integer code();

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Error that = (Error) o;
        return Objects.equals(error, that.error);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(error);
    }

    @Override
    public String toString() {
        return "DomainError{" +
               "code='" + error + '\'' +
               "message='" + super.getMessage() + '\'' +
               '}';
    }
}
