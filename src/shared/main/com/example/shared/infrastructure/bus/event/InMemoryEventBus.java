package com.example.shared.infrastructure.bus.event;

import com.example.shared.domain.bus.event.Event;
import com.example.shared.domain.bus.event.EventBus;
import com.example.shared.infrastructure.ioc.IocContainer;
import com.google.inject.Inject;

public final class InMemoryEventBus implements EventBus {
    private final EventHandlersInformation information;

    @Inject
    public InMemoryEventBus() {
        this.information = new EventHandlersInformation();
    }

    @Override
    public void execute(Event... events) {
        for (var event: events) {
            this.publishEvent(event);
        }
    }

    private void publishEvent(Event event) {
        this.information.search(event.getClass())
                        .stream()
                        .map(IocContainer::getSafeInstance)
                        .forEach(subscriber -> subscriber.execute(event));
    }
}
