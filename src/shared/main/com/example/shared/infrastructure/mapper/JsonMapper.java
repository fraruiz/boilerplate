package com.example.shared.infrastructure.mapper;

import com.example.shared.domain.mappers.Mapper;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.blackbird.BlackbirdModule;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

public final class JsonMapper implements Mapper {
    private final ObjectMapper objectMapper;

    public JsonMapper() {
        this.objectMapper = new ObjectMapper();

        objectMapper.registerModule(new BlackbirdModule());
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.disable(SerializationFeature.WRITE_DATE_KEYS_AS_TIMESTAMPS);
        SimpleDateFormat sdf = new SimpleDateFormat("yyy-MM-dd'T'HH:mm:ss'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        objectMapper.setDateFormat(sdf);
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Override
    public <T> T map(Object from, Class<T> valueType) {
        if (valueType == String.class) {
            try {
                return valueType.cast(this.objectMapper.writeValueAsString(from));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
        if (from instanceof String json) {
            try {
                return this.objectMapper.readValue(json, valueType);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
        return this.objectMapper.convertValue(from, valueType);
    }
}
