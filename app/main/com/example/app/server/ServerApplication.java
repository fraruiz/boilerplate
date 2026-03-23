package com.example.app.server;

import com.example.app.server.handlers.errors.KnowErrorHandler;
import com.example.app.server.handlers.errors.UnknowErrorHandler;
import com.example.app.server.handlers.filters.AfterHandler;
import com.example.app.server.handlers.filters.BeforeHandler;
import com.example.app.server.handlers.maps.HandlersMappers;
import com.example.shared.domain.errors.Error;
import com.example.shared.domain.properties.PropertiesProvider;
import com.example.shared.infrastructure.ioc.IocContainer;
import io.javalin.Javalin;

public class ServerApplication {
    public static void execute() {
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

    private static int getPort() {
        PropertiesProvider provider = IocContainer.getSafeInstance(PropertiesProvider.class);

        return Integer.parseInt(provider.execute("server.port"));
    }
}
