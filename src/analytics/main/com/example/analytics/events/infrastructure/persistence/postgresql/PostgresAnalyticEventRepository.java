package com.example.analytics.events.infrastructure.persistence.postgresql;

import com.example.analytics.events.domain.AnalyticEvent;
import com.example.analytics.events.domain.AnalyticEventRepository;
import com.example.analytics.shared.infrastructure.persistence.AnalyticsDataSource;
import com.example.shared.domain.logs.Logger;
import com.example.shared.domain.mappers.Mapper;
import com.example.shared.infrastructure.persistence.SqlRepository;

public final class PostgresAnalyticEventRepository extends SqlRepository implements AnalyticEventRepository {
    private static final String UPSERT = """
            MERGE INTO analytic_events AS target
            USING (VALUES (?, ?, ?, ?, ?, ?, ?)) AS source(id, aggregate_id, name, body, occurred_on, created_at, updated_at)
            ON target.id = source.id
            WHEN MATCHED THEN UPDATE SET
                aggregate_id = source.aggregate_id,
                name         = source.name,
                body         = CAST(source.body AS jsonb),
                occurred_on  = source.occurred_on,
                updated_at   = source.updated_at
            WHEN NOT MATCHED THEN INSERT (id, aggregate_id, name, body, occurred_on, created_at, updated_at)
            VALUES (source.id, source.aggregate_id, source.name, CAST(source.body AS jsonb), source.occurred_on, source.created_at, source.updated_at)
            """;

    private final Mapper mapper;

    public PostgresAnalyticEventRepository(Logger logger, Mapper mapper, AnalyticsDataSource dataSource) {
        super(logger, dataSource);
        this.mapper = mapper;
    }

    @Override
    public void save(AnalyticEvent analytic) {
        runInsert(UPSERT,
                analytic.id().value(),
                analytic.aggregateId().value(),
                analytic.name().value(),
                mapper.map(analytic.body().value(), String.class),
                analytic.occurredOn().value(),
                analytic.createdAt().value(),
                analytic.updatedAt().value()
        );
    }
}
