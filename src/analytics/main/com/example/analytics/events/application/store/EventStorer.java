package com.example.analytics.events.application.store;

import com.example.analytics.events.domain.AnalyticEvent;
import com.example.analytics.events.domain.AnalyticEventRepository;
import com.example.shared.domain.bus.event.Event;

public final class EventStorer {
    private final AnalyticEventRepository repository;

    public EventStorer(AnalyticEventRepository repository) {
        this.repository = repository;
    }

    public void execute(Event event) {
        AnalyticEvent analytic = AnalyticEvent.factory(event);

        this.repository.save(analytic);
    }
}
