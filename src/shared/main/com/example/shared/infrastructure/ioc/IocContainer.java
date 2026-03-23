package com.example.shared.infrastructure.ioc;

import com.google.inject.Injector;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

public class IocContainer {
    private static final String APP = "APP";
    private static final AtomicReference<IocContainer> IMPLEMENTATION = new AtomicReference<>();
    private static final ConcurrentHashMap<String, Injector> INJECTORS = new ConcurrentHashMap<>();

    private IocContainer() {
    }

    public static void setImplementation(IocContainer iocContainer) {
        IMPLEMENTATION.set(iocContainer);
    }

    public static void addInjector(Injector injector) {
        INJECTORS.put(APP, injector);
    }

    public static void addInjector(String name, Injector injector) {
        INJECTORS.put(name, injector);
    }

    public static Injector getInjector(String name) {
        return INJECTORS.get(name);
    }

    public static <T> T getSafeInstance(Class<T> clazz) {
        return getSafeInstance(APP, clazz);
    }

    public static <T> T getSafeInstance(String injectorName, Class<T> clazz) {
        try {
            Injector injector = getInjector(injectorName);
            return injector == null ? null : injector.getInstance(clazz);
        } catch (Exception e) {
            return null;
        }
    }

    public static void clearInjector(String name) {
        INJECTORS.remove(name);
    }

    public static void clearInjectors() {
        INJECTORS.clear();
    }

    public static IocContainer get() {
        if (IMPLEMENTATION.get() == null) {
            IMPLEMENTATION.compareAndSet(null, new IocContainer());
        }

        return IMPLEMENTATION.get();
    }
}
