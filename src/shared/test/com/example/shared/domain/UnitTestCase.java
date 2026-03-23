package com.example.shared.domain;

import com.example.shared.domain.bus.event.Event;
import com.example.shared.domain.bus.event.EventBus;
import com.example.shared.domain.date.DateProvider;
import com.example.shared.domain.identifiers.IdentifierGenerator;
import com.example.shared.domain.logs.Logger;
import com.example.shared.domain.monitoring.Monitoring;
import com.example.shared.domain.transactions.TransactionManager;
import com.example.shared.domain.valueobjects.Identifier;
import org.junit.jupiter.api.BeforeEach;

import java.time.OffsetDateTime;

import static org.mockito.Mockito.*;

public abstract class UnitTestCase {
    protected final static OffsetDateTime NOW = OffsetDateTime.now();

    protected EventBus eventBus;
    protected IdentifierGenerator identifierGenerator;
    protected DateProvider dateProvider;
    protected Logger logger;
    protected Monitoring monitoring;
    protected TransactionManager transactionManager;

    @BeforeEach
    protected void setUp() {
        this.eventBus = mock(EventBus.class);
        this.dateProvider = mock(DateProvider.class);
        this.identifierGenerator = mock(IdentifierGenerator.class);
        this.logger = mock(Logger.class);
        this.monitoring = mock(Monitoring.class);
        this.transactionManager = Runnable::run;
    }

    public void shouldHavePublished(Event... events) {
        verify(eventBus, atLeastOnce()).execute(events);
    }

    protected void shouldHavePublished(Class<? extends Event> eventClass) {
        verify(eventBus, atLeastOnce()).execute(any(eventClass));
    }

    public <T extends Identifier> void shouldGenerateIdentifier(T id) {
        when(identifierGenerator.execute()).thenReturn(id);
    }

    public void shouldProvideCurrentDate() {
        when(dateProvider.offsetDateTimeNow()).thenReturn(NOW);
    }

    public void shouldProvide(OffsetDateTime dateTime) {
        when(dateProvider.offsetDateTimeNow()).thenReturn(dateTime);
    }
}
