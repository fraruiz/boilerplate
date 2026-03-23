package com.example.shared.infrastructure.identifiers;

import com.example.shared.domain.identifiers.IdentifierGenerator;
import com.example.shared.domain.valueobjects.Identifier;

import java.util.UUID;

public final class UuidIdentifierGenerator<T extends Identifier> implements IdentifierGenerator<T> {
    private final Class<T> type;

    public UuidIdentifierGenerator(Class<T> type) {
        this.type = type;
    }

    @Override
    public T execute() {
        try {
            return type.getDeclaredConstructor(String.class).newInstance(UUID.randomUUID().toString());
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Cannot instantiate " + type.getSimpleName(), e);
        }
    }
}
