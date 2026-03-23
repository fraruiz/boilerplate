package com.example.shared.infrastructure.ioc;

import com.example.shared.domain.bus.command.CommandBus;
import com.example.shared.domain.bus.event.EventBus;
import com.example.shared.domain.bus.query.QueryBus;
import com.example.shared.domain.date.DateProvider;
import com.example.shared.domain.env.Environment;
import com.example.shared.domain.locks.Locker;
import com.example.shared.domain.logs.Logger;
import com.example.shared.domain.mappers.Mapper;
import com.example.shared.domain.monitoring.Monitoring;
import com.example.shared.domain.properties.PropertiesProvider;
import com.example.shared.infrastructure.bus.command.InMemoryCommandBus;
import com.example.shared.infrastructure.bus.event.InMemoryEventBus;
import com.example.shared.infrastructure.bus.query.InMemoryQueryBus;
import com.example.shared.infrastructure.date.DefaultDateProvider;
import com.example.shared.infrastructure.env.SystemEnvironment;
import com.example.shared.infrastructure.locks.InMemoryLocker;
import com.example.shared.infrastructure.logs.Slf4jLogger;
import com.example.shared.infrastructure.mapper.JsonMapper;
import com.example.shared.infrastructure.monitoring.DataDogMonitoring;
import com.example.shared.infrastructure.persistence.DataSourceProvider;

import com.example.shared.infrastructure.properties.DefaultPropertiesProvider;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;


public final class SharedModule extends AbstractModule {

    @Provides
    @Singleton
    public Environment environment() {
        return new SystemEnvironment();
    }

    @Provides
    @Singleton
    public DateProvider dateProvider() {
        return new DefaultDateProvider();
    }

    @Provides
    @Singleton
    public Locker locker() {
        return new InMemoryLocker();
    }

    @Provides
    @Singleton
    public Logger logger(Mapper mapper) {
        return new Slf4jLogger(mapper);
    }

    @Provides
    @Singleton
    public Monitoring monitoring() {
        return new DataDogMonitoring();
    }

    @Provides
    @Singleton
    public CommandBus commandBus() {
        return new InMemoryCommandBus();
    }

    @Provides
    @Singleton
    public EventBus eventBus() {
        return new InMemoryEventBus();
    }

    @Provides
    @Singleton
    public QueryBus queryBus() {
        return new InMemoryQueryBus();
    }

    @Provides
    @Singleton
    public DataSourceProvider dataSourceProvider(Logger logger, Environment environment) {
        return new DataSourceProvider(logger, environment);
    }

    @Provides
    @Singleton
    public PropertiesProvider propertyProvider(Environment environment) {
        return new DefaultPropertiesProvider(environment);
    }

    @Provides
    @Singleton
    public Mapper mapper() {
        return new JsonMapper();
    }
}
