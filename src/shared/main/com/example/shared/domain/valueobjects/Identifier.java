package com.example.shared.domain.valueobjects;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public abstract class Identifier implements Serializable {
    @Serial
    private static final long serialVersionUID = 0L;
    private final String value;

    public Identifier(String value) {
        ensureValidUuid(value);
        this.value = value;
    }

    private void ensureValidUuid(String value) {
        UUID.fromString(value);
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        var that = (Identifier) obj;
        return Objects.equals(this.value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "Identifier[" +
                "value=" + value + ']';
    }

}
