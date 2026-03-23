package com.example.analytics.events.application.store;

import com.example.analytics.events.EventsModuleUnitTestCase;
import com.example.shared.domain.bus.event.Event;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.Serializable;
import java.util.Map;
import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public final class StoreEventOnEventOccurredTest extends EventsModuleUnitTestCase {
    private EventStorer storer;
    private StoreEventOnEventOccurred subscriber;

    @BeforeEach
    @Override
    public void setUp() {
        super.setUp();
        this.storer = mock(EventStorer.class);
        this.subscriber = new StoreEventOnEventOccurred(this.storer);
    }

    @Test
    void should_delegate_to_event_storer_when_event_is_received() {
        Event event = aTestEvent();

        this.subscriber.execute(event);

        verify(this.storer, times(1)).execute(event);
    }

    private static Event aTestEvent() {
        return new Event(UUID.randomUUID().toString()) {
            @Override
            public String event() {
                return "test.event.occurred";
            }

            @Override
            public Map<String, Serializable> toPrimitives() {
                return Map.of();
            }
        };
    }
}
