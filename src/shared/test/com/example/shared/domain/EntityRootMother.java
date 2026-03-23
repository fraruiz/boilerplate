package com.example.shared.domain;

import com.example.shared.domain.valueobjects.DateTime;
import com.example.shared.domain.valueobjects.Identifier;

import java.time.OffsetDateTime;

public abstract class EntityRootMother<T extends EntityRoot<ID>, ID extends Identifier> {
    protected ID identifier;
    protected DateTime createdAt;
    protected DateTime updatedAt;

    protected EntityRootMother(ID identifier, DateTime createdAt, DateTime updatedAt) {
        this.identifier = identifier;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public EntityRootMother<T, ID> withIdentifier(ID identifier) {
        this.identifier = identifier;
        return this;
    }

    public EntityRootMother<T, ID> withCreatedAt(OffsetDateTime offsetDateTime) {
        this.createdAt = new DateTime(offsetDateTime);
        return this;
    }

    public EntityRootMother<T, ID> withUpdatedAt(OffsetDateTime offsetDateTime) {
        this.updatedAt = new DateTime(offsetDateTime);
        return this;
    }

    public abstract T build();
}
