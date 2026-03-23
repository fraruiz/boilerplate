package com.example.shared.domain;

import com.example.shared.domain.bus.event.Event;
import com.example.shared.domain.valueobjects.DateTime;
import com.example.shared.domain.valueobjects.Identifier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class AggregateRoot<ID extends Identifier> extends EntityRoot<ID> {
    private List<Event> domainEvents;

    public AggregateRoot(ID id, DateTime createdAt, DateTime updatedAt) {
        super(id, createdAt, updatedAt);
        this.domainEvents = new ArrayList<>();
    }

    final public Event[] pullDomainEvents() {
        List<Event> events = domainEvents;
        domainEvents = Collections.emptyList();
        return events.toArray(new Event[]{});
    }

    final protected void record(Event event) {
        domainEvents.add(event);
    }
}
