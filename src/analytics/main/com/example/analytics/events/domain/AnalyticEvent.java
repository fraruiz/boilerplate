package com.example.analytics.events.domain;

import com.example.analytics.events.domain.valueobjects.AnalyticAggregateId;
import com.example.analytics.events.domain.valueobjects.AnalyticEventBody;
import com.example.analytics.events.domain.valueobjects.AnalyticEventId;
import com.example.analytics.events.domain.valueobjects.AnalyticEventName;
import com.example.shared.domain.EntityRoot;
import com.example.shared.domain.bus.event.Event;
import com.example.shared.domain.valueobjects.DateTime;

import java.util.Objects;

public final class AnalyticEvent extends EntityRoot<AnalyticEventId> {
    private final AnalyticAggregateId aggregateId;
    private final AnalyticEventName name;
    private final AnalyticEventBody body;
    private final DateTime occurredOn;

    public AnalyticEvent(AnalyticEventId id,
                         AnalyticAggregateId aggregateId,
                         AnalyticEventName name,
                         AnalyticEventBody body,
                         DateTime occurredOn,
                         DateTime createdAt,
                         DateTime updatedAt) {
        super(id, createdAt, updatedAt);
        this.aggregateId = aggregateId;
        this.name = name;
        this.body = body;
        this.occurredOn = occurredOn;
    }

    public static AnalyticEvent factory(Event event) {
        return new AnalyticEvent(
                new AnalyticEventId(event.eventId()),
                new AnalyticAggregateId(event.aggregateId()),
                new AnalyticEventName(event.event()),
                new AnalyticEventBody(event.toPrimitives()),
                new DateTime(event.occurredOn()),
                new DateTime(event.occurredOn()),
                new DateTime(event.occurredOn())
        );
    }

    public AnalyticAggregateId aggregateId() {
        return aggregateId;
    }

    public AnalyticEventName name() {
        return name;
    }

    public AnalyticEventBody body() {
        return body;
    }

    public DateTime occurredOn() {
        return occurredOn;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        AnalyticEvent that = (AnalyticEvent) o;
        return Objects.equals(aggregateId, that.aggregateId) && Objects.equals(name, that.name) && Objects.equals(body, that.body) &&
               Objects.equals(occurredOn, that.occurredOn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), aggregateId, name, body, occurredOn);
    }

    @Override
    public String toString() {
        return "AnalyticEvent{" +
               "aggregateId=" + aggregateId +
               ", name=" + name +
               ", body=" + body +
               ", occurredOn=" + occurredOn +
               ", id=" + id +
               ", createdAt=" + createdAt +
               ", updatedAt=" + updatedAt +
               '}';
    }
}
