package com.example.shared.infrastructure.logs;

import com.example.shared.domain.logs.Logger;
import com.example.shared.domain.logs.RequestContext;
import com.example.shared.domain.mappers.Mapper;
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
        LOGGER.info(withRequestId(message));
    }

    @Override
    public void info(String message, Map<String, Serializable> context) {
        this.info(String.format("%s. Context: %s", message, mapper.map(context, String.class)));
    }

    @Override
    public void warning(String message) {
        LOGGER.warn(withRequestId(message));
    }

    @Override
    public void warning(String message, Map<String, Serializable> context) {
        this.warning(String.format("%s. Context: %s", message, mapper.map(context, String.class)));
    }

    @Override
    public void critical(String message) {
        LOGGER.error(withRequestId(message));
    }

    private String withRequestId(String message) {
        String requestId = RequestContext.getRequestId();
        return requestId != null ? "[" + requestId + "] " + message : message;
    }

    @Override
    public void critical(String message, Map<String, Serializable> context) {
        this.critical(String.format("%s. Context: %s", message, mapper.map(context, String.class)));
    }
}
