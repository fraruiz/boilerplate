package com.example.app.server.handlers;

import com.example.shared.domain.bus.command.CommandBus;
import com.example.shared.domain.bus.query.QueryBus;
import com.example.shared.domain.logs.Logger;
import com.example.shared.domain.mappers.Mapper;
import com.example.shared.infrastructure.ioc.IocContainer;
import io.javalin.http.ContentType;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.http.HttpStatus;
import org.eclipse.jetty.http.HttpMethod;

public abstract class RequestHandler implements Handler {
    protected final QueryBus queryBus;
    protected final CommandBus commandBus;
    protected final Mapper mapper;
    protected final Logger logger;

    public RequestHandler() {
        this.queryBus = IocContainer.getSafeInstance(QueryBus.class);
        this.commandBus = IocContainer.getSafeInstance(CommandBus.class);
        this.mapper = IocContainer.getSafeInstance(Mapper.class);
        this.logger = IocContainer.getSafeInstance(Logger.class);
    }

    public abstract String path();

    public abstract HttpMethod method();

    protected void response(Context context, HttpStatus status) {
        context.status(status);
        context.contentType(ContentType.APPLICATION_JSON);
    }

    protected void response(Context context, HttpStatus status, Object response) {
        String result = this.mapper.map(response, String.class);

        context.result(result);
        context.status(status);
        context.contentType(ContentType.APPLICATION_JSON);
    }
}
