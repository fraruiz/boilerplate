package com.example.shared.domain.bus.event;

public interface EventBus {
    void execute(Event... events);
}
