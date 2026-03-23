package com.example.app;

import com.example.analytics.shared.infrastructure.ioc.AnalyticsModule;
import com.example.app.handlers.maps.HandlersMappers;
import com.example.app.handlers.errors.KnowErrorHandler;
import com.example.app.handlers.errors.UnknowErrorHandler;
import com.example.app.handlers.filters.AfterHandler;
import com.example.app.handlers.filters.BeforeHandler;
import com.example.shared.domain.errors.Error;
import com.example.shared.domain.mappers.Mapper;
import com.example.shared.domain.properties.PropertiesProvider;
import com.example.shared.infrastructure.ioc.IocContainer;
import com.example.shared.infrastructure.ioc.SharedModule;
import com.google.inject.Guice;
import io.javalin.Javalin;

public class Starter {
    static void main() {
        IocContainer.addInjector(Guice.createInjector(new SharedModule(), new AnalyticsModule()));

        int port = getPort();

        Javalin.create(config -> {
            config.http.asyncTimeout = 10_000L;

            config.routes.exception(Error.class, new KnowErrorHandler());
            config.routes.exception(Exception.class, new UnknowErrorHandler());
            config.routes.before(new BeforeHandler());
            config.routes.after(new AfterHandler());
            config.routes.apiBuilder(new HandlersMappers());
        }).start(port);
    }

    private static Mapper getMapper() {
        return IocContainer.getSafeInstance(Mapper.class);
    }

    private static int getPort() {
        PropertiesProvider provider = IocContainer.getSafeInstance(PropertiesProvider.class);

        return Integer.parseInt(provider.execute("server.port"));
    }
}
