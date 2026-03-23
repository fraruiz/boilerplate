package com.example.analytics.events;

import com.example.analytics.AnalyticsContextUnitTestCase;
import com.example.analytics.events.domain.AnalyticEvent;
import com.example.analytics.events.domain.AnalyticEventRepository;
import org.junit.jupiter.api.BeforeEach;

import static org.mockito.Mockito.*;

public abstract class EventsModuleUnitTestCase extends AnalyticsContextUnitTestCase {
    protected AnalyticEventRepository repository;

    @BeforeEach
    @Override
    public void setUp() {
        super.setUp();
        this.repository = mock(AnalyticEventRepository.class);
    }

    public void shouldHaveSaved(AnalyticEvent event) {
        verify(this.repository, atLeastOnce()).save(event);
    }

    public void shouldHaveNotSave() {
        verify(repository, never()).save(any());
    }
}
