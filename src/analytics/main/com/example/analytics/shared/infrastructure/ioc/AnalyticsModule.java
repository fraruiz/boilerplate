package com.example.analytics.shared.infrastructure.ioc;

import com.example.analytics.events.application.store.EventStorer;
import com.example.analytics.events.application.store.StoreEventOnEventOccurred;
import com.example.analytics.events.domain.AnalyticEventRepository;
import com.example.analytics.events.infrastructure.persistence.postgresql.PostgresAnalyticEventRepository;
import com.example.analytics.shared.infrastructure.persistence.AnalyticsDataSource;
import com.example.analytics.shared.infrastructure.persistence.AnalyticsSqlTransactionManager;
import com.example.shared.domain.logs.Logger;
import com.example.shared.domain.mappers.Mapper;
import com.example.shared.domain.properties.PropertiesProvider;
import com.example.shared.infrastructure.persistence.DataSourceProvider;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

public final class AnalyticsModule extends AbstractModule {

    @Provides
    @Singleton
    public AnalyticsDataSource analyticsDataSource(DataSourceProvider provider, PropertiesProvider properties) {
        return new AnalyticsDataSource(provider.execute(
            properties.execute("analytics.db.name"),
            properties.execute("analytics.db.host"),
            Integer.parseInt(properties.execute("analytics.db.port")),
            properties.execute("analytics.db.dbms"),
            properties.execute("analytics.db.username"),
            properties.execute("analytics.db.password"),
            Integer.parseInt(properties.execute("analytics.db.pool.max")),
            Integer.parseInt(properties.execute("analytics.db.pool.idle")),
            Integer.parseInt(properties.execute("analytics.db.pool.idle_timeout")),
            Integer.parseInt(properties.execute("analytics.db.pool.connection_timeout")),
            false
        ));
    }

    @Provides
    @Singleton
    public AnalyticsSqlTransactionManager analyticsContextSqlTransactionManager(AnalyticsDataSource dataSource) {
        return new AnalyticsSqlTransactionManager(dataSource);
    }

    @Provides
    @Singleton
    public AnalyticEventRepository analyticEventRepository(Logger logger, Mapper mapper, AnalyticsDataSource dataSource) {
        return new PostgresAnalyticEventRepository(logger, mapper, dataSource);
    }

    @Provides
    @Singleton
    public EventStorer eventStorer(AnalyticEventRepository repository) {
        return new EventStorer(repository);
    }

    @Provides
    @Singleton
    public StoreEventOnEventOccurred storeEventOnEventOccurred(EventStorer storer) {
        return new StoreEventOnEventOccurred(storer);
    }
}
