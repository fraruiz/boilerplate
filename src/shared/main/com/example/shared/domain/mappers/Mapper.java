package com.example.shared.domain.mappers;

public interface Mapper {
    <T> T map(Object from, Class<T> valueType);
}
