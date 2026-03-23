package com.example.shared.domain;

import com.example.shared.domain.valueobjects.DateTime;
import com.example.shared.domain.valueobjects.Identifier;

import java.util.Objects;

public abstract class EntityRoot<ID extends Identifier> {
    protected final ID id;
    protected final DateTime createdAt;
    protected final DateTime updatedAt;

    public EntityRoot(ID id, DateTime createdAt, DateTime updatedAt) {
        this.id = id;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public ID id() {
        return id;
    }

    public DateTime createdAt() {
        return createdAt;
    }

    public DateTime updatedAt() {
        return updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        EntityRoot<?> that = (EntityRoot<?>) o;
        return Objects.equals(id, that.id) && Objects.equals(createdAt,
                                                             that.createdAt) && Objects.equals(
                updatedAt,
                that.updatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, createdAt, updatedAt);
    }

    @Override
    public String toString() {
        return "EntityRoot{" +
                "id=" + id +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
