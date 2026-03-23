package com.example.app.server.handlers.filters;

import com.example.shared.domain.bus.command.CommandBus;
import com.example.shared.domain.bus.query.QueryBus;
import com.example.shared.domain.logs.Logger;
import com.example.shared.domain.mappers.Mapper;
import com.example.shared.domain.monitoring.Monitoring;
import com.example.shared.infrastructure.ioc.IocContainer;
import io.javalin.http.Handler;

public abstract class MiddlewareHandler implements Handler {
    protected final QueryBus queryBus;
    protected final CommandBus commandBus;
    protected final Mapper mapper;
    protected final Logger logger;
    protected final Monitoring monitoring;

    public MiddlewareHandler() {
        this.queryBus = IocContainer.getSafeInstance(QueryBus.class);
        this.commandBus = IocContainer.getSafeInstance(CommandBus.class);
        this.mapper = IocContainer.getSafeInstance(Mapper.class);
        this.logger = IocContainer.getSafeInstance(Logger.class);
        this.monitoring = IocContainer.getSafeInstance(Monitoring.class);
    }
}
