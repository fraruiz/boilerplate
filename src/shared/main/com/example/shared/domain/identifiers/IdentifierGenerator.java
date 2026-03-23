package com.example.shared.domain.identifiers;

import com.example.shared.domain.valueobjects.Identifier;

public interface IdentifierGenerator<T extends Identifier> {
    T execute();
}
