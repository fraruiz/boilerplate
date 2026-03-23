package com.example.app.workers.handlers.maps;

import com.example.app.workers.handlers.WorkHandler;
import com.example.shared.domain.logs.Logger;
import com.example.shared.domain.logs.RequestContext;
import com.example.shared.domain.monitoring.Monitoring;
import com.example.shared.infrastructure.ioc.IocContainer;
import org.reflections.Reflections;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class WorkHandlersMapper {
    private static final String METRIC_EXECUTIONS = "worker.executions";
    private static final String METRIC_ACTIVE = "worker.active";

    private final List<? extends WorkHandler> workers;
    private final Logger logger;
    private final Monitoring monitoring;

    public WorkHandlersMapper() {
        Reflections reflections = new Reflections(WorkHandler.class.getPackageName());
        this.workers = loadWorkers(reflections);
        this.logger = IocContainer.getSafeInstance(Logger.class);
        this.monitoring = IocContainer.getSafeInstance(Monitoring.class);
    }

    public void schedule(ScheduledExecutorService scheduler) {
        for (WorkHandler workHandler : workers) {
            long initialDelay = workHandler.initialDelay().toMillis();
            long period = workHandler.period().toMillis();
            String workerName = workHandler.getClass().getSimpleName();

            scheduler.scheduleAtFixedRate(
                () -> run(workHandler, workerName),
                initialDelay, period, TimeUnit.MILLISECONDS
            );
        }
    }

    private void run(WorkHandler workHandler, String workerName) {
        RequestContext.set(UUID.randomUUID().toString(), "WORKER", workerName);
        long startTime = System.currentTimeMillis();

        monitoring.incrementGauge(METRIC_ACTIVE, Map.<String, Serializable>of("worker", workerName));

        try {
            logger.info("Worker started", Map.of());
            workHandler.execute();

            long durationMs = System.currentTimeMillis() - startTime;
            logger.info("Worker completed", Map.<String, Serializable>of("durationMs", durationMs));
            monitoring.incrementCounter(METRIC_EXECUTIONS, Map.<String, Serializable>of("worker", workerName, "status", "success"));
        } catch (Exception e) {
            long durationMs = System.currentTimeMillis() - startTime;
            logger.critical("Worker failed", Map.<String, Serializable>of("durationMs", durationMs, "error", e.getMessage()));
            monitoring.incrementCounter(METRIC_EXECUTIONS, Map.<String, Serializable>of("worker", workerName, "status", "failed"));
        } finally {
            monitoring.decrementGauge(METRIC_ACTIVE, Map.<String, Serializable>of("worker", workerName));
            RequestContext.clear();
        }
    }

    private List<? extends WorkHandler> loadWorkers(Reflections reflections) {
        return reflections.getSubTypesOf(WorkHandler.class).stream()
                          .map(clazz -> {
                              try {
                                  return clazz.getDeclaredConstructor().newInstance();
                              } catch (InstantiationException | IllegalAccessException |
                                       InvocationTargetException | NoSuchMethodException e) {
                                  throw new RuntimeException("Failed to instantiate worker: " + clazz.getName(), e);
                              }
                          })
                          .toList();
    }
}
