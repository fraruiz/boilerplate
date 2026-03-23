package com.example.analytics.events.application.store;

import com.example.analytics.events.EventsModuleUnitTestCase;
import com.example.analytics.events.domain.AnalyticEvent;
import com.example.analytics.events.domain.AnalyticEventMother;
import com.example.shared.domain.bus.event.Event;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.Serializable;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

public final class EventStorerTest extends EventsModuleUnitTestCase {
    private EventStorer storer;

    @BeforeEach
    @Override
    public void setUp() {
        super.setUp();
        this.storer = new EventStorer(this.repository);
    }

    @Test
    void should_save_analytic_event_when_event_is_received() {
        Event event = aTestEvent();
        AnalyticEvent expected = AnalyticEventMother.from(event).build();

        this.storer.execute(event);

        shouldHaveSaved(expected);
    }

    private static Event aTestEvent() {
        return new Event(UUID.randomUUID().toString()) {
            @Override
            public String event() {
                return "test.event.occurred";
            }

            @Override
            public Map<String, Serializable> toPrimitives() {
                return Map.of("key", "value");
            }
        };
    }
}
