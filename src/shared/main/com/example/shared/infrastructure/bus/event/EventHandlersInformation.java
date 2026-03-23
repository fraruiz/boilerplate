package com.example.shared.infrastructure.bus.event;

import com.example.shared.domain.bus.command.CommandNotRegisteredError;
import com.example.shared.domain.bus.event.Event;
import com.example.shared.domain.bus.event.EventHandler;
import com.example.shared.infrastructure.bus.command.CommandHandlersInformation;
import org.reflections.Reflections;

import java.lang.reflect.ParameterizedType;
import java.util.*;

public final class EventHandlersInformation {
    private final Map<Class<? extends Event>, List<Class<? extends EventHandler>>> indexedHandlers;

    public EventHandlersInformation() {
        String basePackage = extractBasePackage();
        Reflections reflections = new Reflections(basePackage);
        Set<Class<? extends EventHandler>> classes = reflections.getSubTypesOf(EventHandler.class);

        this.indexedHandlers = formatHandlers(classes);
    }

    public List<Class<? extends EventHandler>> search(Class<? extends Event> eventClass) throws CommandNotRegisteredError {
        return this.indexedHandlers.get(eventClass);
    }

    private Map<Class<? extends Event>, List<Class<? extends EventHandler>>> formatHandlers(Set<Class<? extends EventHandler>> eventHandlers) {
        Map<Class<? extends Event>, List<Class<? extends EventHandler>>> handlers = new HashMap<>();

        for (Class<? extends EventHandler> handler : eventHandlers) {
            ParameterizedType paramType    = (ParameterizedType) handler.getGenericInterfaces()[0];
            Class<? extends Event> key = (Class<? extends Event>) paramType.getActualTypeArguments()[0];

            List<Class<? extends EventHandler>> value;
            if (handlers.containsKey(key)) {
                value = handlers.get(key);
            } else {
                value = new ArrayList<>();
            }

            value.add(handler);

            handlers.put(key, value);
        }

        return handlers;
    }

    private String extractBasePackage() {
        String packageName = CommandHandlersInformation.class.getPackageName();
        String[] parts = packageName.split("\\.");
        return parts.length >= 2 ? parts[0] + "." + parts[1] : parts[0];
    }
}
