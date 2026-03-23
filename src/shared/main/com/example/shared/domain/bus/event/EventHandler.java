package com.example.shared.domain.bus.event;

public interface EventHandler<T extends Event> {
    void execute(T event);

    Class<T> event();
}
