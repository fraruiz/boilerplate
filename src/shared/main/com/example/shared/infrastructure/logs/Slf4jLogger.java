package com.example.shared.infrastructure.logs;

import com.example.shared.domain.logs.Logger;
import com.example.shared.domain.mappers.Mapper;
import net.logstash.logback.argument.StructuredArguments;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Map;

public final class Slf4jLogger implements Logger {
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(Slf4jLogger.class);

    private final Mapper mapper;

    public Slf4jLogger(Mapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public void info(String message) {
        LOGGER.info(message);
    }

    @Override
    public void info(String message, Map<String, Serializable> context) {
        LOGGER.info(message, StructuredArguments.entries(context));
    }

    @Override
    public void warning(String message) {
        LOGGER.warn(message);
    }

    @Override
    public void warning(String message, Map<String, Serializable> context) {
        LOGGER.warn(message, StructuredArguments.entries(context));
    }

    @Override
    public void critical(String message) {
        LOGGER.error(message);
    }

    @Override
    public void critical(String message, Map<String, Serializable> context) {
        LOGGER.error(message, StructuredArguments.entries(context));
    }
}
