package com.example.shared.domain;

import com.example.shared.domain.valueobjects.DateTime;
import com.example.shared.domain.valueobjects.Identifier;

public abstract class AggregateRootRootMother<T extends AggregateRoot<ID>, ID extends Identifier> extends EntityRootMother<T, ID> {
    protected AggregateRootRootMother(ID identifier, DateTime createdAt, DateTime updatedAt) {
        super(identifier, createdAt, updatedAt);
    }
}
