package com.example.analytics.events.application.store;

import com.example.shared.domain.bus.event.Event;
import com.example.shared.domain.bus.event.EventHandler;

public final class StoreEventOnEventOccurred implements EventHandler<Event> {
    private final EventStorer storer;

    public StoreEventOnEventOccurred(EventStorer storer) {
        this.storer = storer;
    }

    @Override
    public void execute(Event event) {
        this.storer.execute(event);
    }

    @Override
    public Class<Event> event() {
        return Event.class;
    }
}
