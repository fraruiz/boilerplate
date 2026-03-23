package com.example.app.handlers.maps;

import com.example.app.handlers.RequestHandler;
import io.javalin.apibuilder.EndpointGroup;
import org.eclipse.jetty.http.HttpMethod;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import static io.javalin.apibuilder.ApiBuilder.*;

public class HandlersMappers implements EndpointGroup {
    private final List<? extends RequestHandler> handlers;

    public HandlersMappers() {
        Reflections reflections = new Reflections(HandlersMappers.class.getPackageName());
        this.handlers = loadHandlers(reflections);
    }

    @Override
    public void addEndpoints() {
        for (RequestHandler handler : handlers) {
            String path = handler.path();
            HttpMethod method = handler.method();

            if (method == HttpMethod.GET) get(path, handler);
            else if (method == HttpMethod.POST) post(path, handler);
            else if (method == HttpMethod.PUT) put(path, handler);
            else if (method == HttpMethod.DELETE) delete(path, handler);
            else if (method == HttpMethod.PATCH) patch(path, handler);

            else throw new IllegalArgumentException("Unsupported HTTP method: " + method);
        }
    }

    private List<? extends RequestHandler> loadHandlers(Reflections reflections) {
        return reflections.getSubTypesOf(RequestHandler.class).stream()
                          .map(clazz -> {
                              try {
                                  return clazz.getDeclaredConstructor().newInstance();
                              } catch (InstantiationException | IllegalAccessException |
                                       InvocationTargetException | NoSuchMethodException e) {
                                  throw new RuntimeException("Failed to instantiate handler: " + clazz.getName(), e);
                              }
                          })
                          .toList();
    }
}
