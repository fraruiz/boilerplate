package com.example.shared.domain.valueobjects;

import java.io.Serializable;
import java.util.Objects;

public abstract class IntegerValueObject implements Serializable {
    private final Integer value;

    public IntegerValueObject(Integer value) {
        this.value = value;
    }

    public Integer value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        IntegerValueObject that = (IntegerValueObject) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    @Override
    public String toString() {
        return "IntegerValueObject{" +
               "value='" + value + '\'' +
               '}';
    }
}
