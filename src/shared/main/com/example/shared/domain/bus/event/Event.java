package com.example.shared.domain.bus.event;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public abstract class Event {
    private final String aggregateId;
    private final String eventId;
    private final OffsetDateTime occurredOn;

    public Event(String aggregateId) {
        this.aggregateId = aggregateId;
        this.eventId = UUID.randomUUID().toString();
        this.occurredOn = OffsetDateTime.now();
    }

    protected Event(String aggregateId, String eventId, OffsetDateTime occurredOn) {
        this.aggregateId = aggregateId;
        this.eventId = eventId;
        this.occurredOn = occurredOn;
    }

    public abstract String event();

    public abstract Map<String, Serializable> toPrimitives();

    public String aggregateId() {
        return aggregateId;
    }

    public String eventId() {
        return eventId;
    }

    public OffsetDateTime occurredOn() {
        return occurredOn;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return Objects.equals(aggregateId, event.aggregateId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(aggregateId, eventId, occurredOn);
    }

    @Override
    public String toString() {
        return "Event{" +
               "aggregateId='" + aggregateId + '\'' +
               ", eventId='" + eventId + '\'' +
               ", occurredOn='" + occurredOn + '\'' +
               '}';
    }
}
