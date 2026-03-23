package com.example.analytics.events.domain;

import com.example.analytics.events.domain.valueobjects.AnalyticAggregateId;
import com.example.analytics.events.domain.valueobjects.AnalyticEventBody;
import com.example.analytics.events.domain.valueobjects.AnalyticEventId;
import com.example.analytics.events.domain.valueobjects.AnalyticEventName;
import com.example.shared.domain.bus.event.Event;
import com.example.shared.domain.valueobjects.DateTime;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

public final class AnalyticEventMother {
    private AnalyticEventId id;
    private AnalyticAggregateId aggregateId;
    private AnalyticEventName name;
    private AnalyticEventBody body;
    private DateTime occurredOn;
    private DateTime createdAt;
    private DateTime updatedAt;

    public AnalyticEventMother() {
        this.id = new AnalyticEventId(UUID.randomUUID().toString());
        this.aggregateId = new AnalyticAggregateId(UUID.randomUUID().toString());
        this.name = new AnalyticEventName("test.event.occurred");
        this.body = new AnalyticEventBody(Map.of("key", "value"));
        this.occurredOn = new DateTime(OffsetDateTime.now());
        this.createdAt = new DateTime(OffsetDateTime.now());
        this.updatedAt = new DateTime(OffsetDateTime.now());
    }

    public static AnalyticEventMother random() {
        return new AnalyticEventMother();
    }

    public static AnalyticEventMother from(AnalyticEvent event) {
        return new AnalyticEventMother()
                .withId(event.id())
                .withAggregateId(event.aggregateId())
                .withName(event.name())
                .withBody(event.body())
                .withOccurredOn(event.occurredOn())
                .withCreatedAt(event.createdAt())
                .withUpdatedAt(event.updatedAt());
    }

    public static AnalyticEventMother from(Event event) {
        return new AnalyticEventMother()
                .withId(new AnalyticEventId(event.eventId()))
                .withAggregateId(new AnalyticAggregateId(event.aggregateId()))
                .withName(new AnalyticEventName(event.event()))
                .withBody(new AnalyticEventBody(event.toPrimitives()))
                .withOccurredOn(new DateTime(event.occurredOn()))
                .withCreatedAt(new DateTime(event.occurredOn()))
                .withUpdatedAt(new DateTime(event.occurredOn()));
    }

    public AnalyticEventMother withId(AnalyticEventId id) {
        this.id = id;
        return this;
    }

    public AnalyticEventMother withAggregateId(AnalyticAggregateId aggregateId) {
        this.aggregateId = aggregateId;
        return this;
    }

    public AnalyticEventMother withName(AnalyticEventName name) {
        this.name = name;
        return this;
    }

    public AnalyticEventMother withBody(AnalyticEventBody body) {
        this.body = body;
        return this;
    }

    public AnalyticEventMother withOccurredOn(DateTime occurredOn) {
        this.occurredOn = occurredOn;
        return this;
    }

    public AnalyticEventMother withCreatedAt(DateTime createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public AnalyticEventMother withUpdatedAt(DateTime updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }

    public AnalyticEvent build() {
        return new AnalyticEvent(id, aggregateId, name, body, occurredOn, createdAt, updatedAt);
    }
}
