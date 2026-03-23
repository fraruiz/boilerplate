package com.example.analytics;

import com.example.analytics.events.domain.AnalyticEvent;
import com.example.analytics.events.domain.valueobjects.AnalyticAggregateId;
import com.example.analytics.events.domain.valueobjects.AnalyticEventBody;
import com.example.analytics.events.domain.valueobjects.AnalyticEventId;
import com.example.analytics.events.domain.valueobjects.AnalyticEventName;
import com.example.analytics.shared.infrastructure.persistence.AnalyticsDataSource;
import com.example.shared.domain.valueobjects.DateTime;
import com.example.shared.infrastructure.InfrastructureTestCase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public abstract class AnalyticsIocContainerInfrastructureTestCase extends InfrastructureTestCase {
    protected AnalyticsDataSource contextDataSource;

    @BeforeEach
    protected void setUp() {
        super.setUp();
        this.contextDataSource = new AnalyticsDataSource(super.dataSource);
    }

    @AfterEach
    protected void tearDown() {
        super.tearDown();
    }

    protected void insert(AnalyticEvent event) {
        insert(
            "INSERT INTO analytic_events (id, aggregate_id, name, body, occurred_on, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?)",
            event.id().value(),
            event.aggregateId().value(),
            event.name().value(),
            mapper.map(event.body().value(), String.class),
            event.occurredOn().value(),
            event.createdAt().value(),
            event.updatedAt().value()
        );
    }

    protected Optional<AnalyticEvent> find(AnalyticEventId id) {
        return query(
            "SELECT id, aggregate_id, name, body, occurred_on, created_at, updated_at FROM analytic_events WHERE id = ?",
            rs -> rs.next() ? Optional.of(mapRow(rs)) : Optional.empty(),
            id.value()
        );
    }

    protected List<AnalyticEvent> search() {
        return query(
            "SELECT id, aggregate_id, name, body, occurred_on, created_at, updated_at FROM analytic_events",
            rs -> {
                List<AnalyticEvent> events = new ArrayList<>();
                while (rs.next()) {
                    events.add(mapRow(rs));
                }
                return events;
            }
        );
    }

    @SuppressWarnings("unchecked")
    private AnalyticEvent mapRow(ResultSet rs) throws SQLException {
        return new AnalyticEvent(
            new AnalyticEventId(rs.getString("id")),
            new AnalyticAggregateId(rs.getString("aggregate_id")),
            new AnalyticEventName(rs.getString("name")),
            new AnalyticEventBody((Map<String, Serializable>) mapper.map(rs.getString("body"), Map.class)),
            new DateTime(rs.getObject("occurred_on", OffsetDateTime.class)),
            new DateTime(rs.getObject("created_at", OffsetDateTime.class)),
            new DateTime(rs.getObject("updated_at", OffsetDateTime.class))
        );
    }
}
