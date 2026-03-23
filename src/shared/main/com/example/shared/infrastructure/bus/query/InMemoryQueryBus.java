package com.example.shared.infrastructure.bus.query;

import com.example.shared.domain.bus.query.*;
import com.example.shared.infrastructure.ioc.IocContainer;
import com.google.inject.Inject;

public final class InMemoryQueryBus implements QueryBus {
    private final QueryHandlersInformation information;

    @Inject
    public InMemoryQueryBus() {
        this.information = new QueryHandlersInformation();
    }

    @Override
    public Response ask(Query query) throws QueryHandlerExecutionError {
        try {
            Class<? extends QueryHandler> queryHandlerClass = information.search(query.getClass());

            QueryHandler handler = IocContainer.getSafeInstance(queryHandlerClass);

            return handler.execute(query);
        } catch (Throwable error) {
            throw new QueryHandlerExecutionError(error);
        }
    }
}
