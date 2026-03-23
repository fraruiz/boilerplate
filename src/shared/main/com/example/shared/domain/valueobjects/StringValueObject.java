package com.example.shared.domain.valueobjects;

import com.example.shared.domain.errors.client.InvalidArgument;

import java.io.Serializable;
import java.util.Objects;

public abstract class StringValueObject implements Serializable {
    private final String value;

    public StringValueObject(String value) {
        this.value = value;
    }

    protected void ensureValueIsNotBlank(String errorMessage) {
        if (this.value.isBlank()) {
            throw new InvalidArgument(errorMessage);
        }
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        StringValueObject that = (StringValueObject) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    @Override
    public String toString() {
        return "StringValueObject{" +
               "value='" + value + '\'' +
               '}';
    }
}
