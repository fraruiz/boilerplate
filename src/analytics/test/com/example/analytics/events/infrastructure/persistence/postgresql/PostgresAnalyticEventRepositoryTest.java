package com.example.analytics.events.infrastructure.persistence.postgresql;

import com.example.analytics.AnalyticsIocContainerInfrastructureTestCase;
import com.example.analytics.events.domain.AnalyticEvent;
import com.example.analytics.events.domain.AnalyticEventMother;
import com.example.analytics.events.domain.AnalyticEventNameMother;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public final class PostgresAnalyticEventRepositoryTest extends AnalyticsIocContainerInfrastructureTestCase {
    private PostgresAnalyticEventRepository repository;

    @BeforeEach
    @Override
    protected void setUp() {
        super.setUp();
        this.repository = new PostgresAnalyticEventRepository(super.logger, super.mapper, super.contextDataSource);
    }

    @Test
    void should_save() {
        AnalyticEvent event = AnalyticEventMother.random().build();

        this.repository.save(event);

        Optional<AnalyticEvent> found = find(event.id());
        assertTrue(found.isPresent());
        assertEquals(event, found.get());
    }

    @Test
    void should_update_on_duplicate_id() {
        AnalyticEvent preset = AnalyticEventMother.random().build();
        AnalyticEvent expected = AnalyticEventMother.from(preset).withName(AnalyticEventNameMother.random()).build();

        super.insert(preset);

        this.repository.save(expected);

        Optional<AnalyticEvent> actual = find(preset.id());

        assertTrue(actual.isPresent());
        assertEquals(expected, actual.get());
    }
}
