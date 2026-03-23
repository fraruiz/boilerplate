package com.example.app.workers.handlers;

import com.example.shared.domain.bus.command.CommandBus;
import com.example.shared.domain.bus.query.QueryBus;
import com.example.shared.domain.logs.Logger;
import com.example.shared.domain.mappers.Mapper;
import com.example.shared.infrastructure.ioc.IocContainer;

import java.time.Duration;

public abstract class WorkHandler {
    protected final QueryBus queryBus;
    protected final CommandBus commandBus;
    protected final Mapper mapper;
    protected final Logger logger;

    public WorkHandler() {
        this.queryBus = IocContainer.getSafeInstance(QueryBus.class);
        this.commandBus = IocContainer.getSafeInstance(CommandBus.class);
        this.mapper = IocContainer.getSafeInstance(Mapper.class);
        this.logger = IocContainer.getSafeInstance(Logger.class);
    }

    public abstract void execute();

    public abstract Duration period();

    public Duration initialDelay() {
        return Duration.ZERO;
    }
}
