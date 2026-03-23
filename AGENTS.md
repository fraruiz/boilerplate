# AGENTS.md

This file provides guidance for AI coding agents working in this repository.

## Project Overview

Java 25 boilerplate implementing **DDD + CQRS + event-driven architecture** using Javalin, Guice, and PostgreSQL. Multi-module Gradle project.

## Module Structure

```
boilerplate/
├── src/shared/       # Core library: domain abstractions, buses, infra, DI
├── src/analytics/    # Example feature module (event persistence)
└── app/              # Main app entry point (Javalin HTTP server)
```

Each module under `src/` follows the layout:
```
<module>/
├── main/com/example/<module>/
│   ├── domain/           # Entities, value objects, repository interfaces
│   ├── application/      # Command/query/event handlers, use cases
│   └── infrastructure/   # DB, HTTP, DI module
└── test/com/example/<module>/
```

## Architecture Patterns

### CQRS Buses
- **CommandBus** → mutates state. Handlers implement `CommandHandler<C extends Command>`.
- **QueryBus** → reads state. Handlers implement `QueryHandler<Q extends Query, R extends Response>`.
- **EventBus** → broadcasts domain events. Handlers implement `EventHandler<E extends Event>`.

All buses are in-memory (`InMemoryCommandBus`, `InMemoryQueryBus`, `InMemoryEventBus`) and auto-discover handlers via classpath scanning (Reflections library).

### Dependency Injection (Guice)
- `IocContainer` holds named `Injector` instances.
- `SharedModule` provides core singletons (buses, date, mapper, logger, monitoring, resilience).
- Each feature module provides its own Guice `Module` (e.g., `AnalyticsModule`).
- Modules are registered in `Starter.java`.

### HTTP Handlers
- Extend `RequestHandler` and annotate with the route path.
- Auto-discovered and registered by `HandlersMappers` at startup.

### Workers
- Extend `WorkHandler` (in `app/workers/handlers/`) and implement `execute()` and `period()`.
- Auto-discovered and scheduled by `WorkHandlersMapper` using `ScheduledExecutorService`.
- Each execution gets a unique `requestId` via `RequestContext` (SLF4J MDC) — propagated to all logs inside `execute()`.
- Metrics emitted per execution: `worker.executions{worker, status=success|failed}` (counter) and `worker.active{worker}` (gauge).
- A minimal Javalin server starts on `worker.port` (default `8081`) exposing `GET /metrics` for Prometheus scraping.

### Repository Pattern
- Extend `SqlRepository<T>` using commons-dbutils `QueryRunner`.
- Use `TransactionContext` (thread-local) for implicit transaction passing.
- H2 for tests; PostgreSQL for production.

### REST Client
- Extend `RestClient` for outgoing HTTP calls.
- Built-in circuit breaker + retry via Resilience4j.

### Domain Model
- `AggregateRoot`: records and pulls domain events.
- `EntityRoot`: immutable entity with timestamps.
- Value objects: `Identifier` (UUID), `StringValueObject`, `Email`, `Url`, `DateTime`, `DateTimeRange`.

## Testing

| Base class | Purpose |
|---|---|
| `UnitTestCase` | Mocked buses, date, logger, monitoring |
| `InfrastructureTestCase` | H2 in-memory DB, schema setup/teardown |
| `<Context>UnitTestCase` | Module-specific unit base (extends UnitTestCase) |

Use **Mother pattern** for test data (`AnalyticEventMother`, `DateTimeMother`, etc.).

Run all tests:
```bash
./gradlew test
```

Run a specific subproject:
```bash
./gradlew :shared:test
./gradlew :analytics:test
```

## Build & CI

```bash
./gradlew build        # compile + test
./gradlew :app:run     # run the application
```

CI runs on GitHub Actions (`.github/workflows/ci.yml`) on push to `main` and on pull requests. Uses JDK 25 (Temurin) with Gradle cache.

## Key Conventions

- **One handler per command or query** — the bus enforces this.
- **Multiple handlers per event** — all are invoked.
- **Environment config** — properties files per environment: `application-local.properties`, `application-stage.properties`, `application-production.properties`. Resolved via `SystemEnvironment`.
- **Structured logging** — Logback + Logstash JSON encoder. Use the injected `Logger` abstraction, not `System.out`.
- **Errors** — throw domain `Error` subclasses; the HTTP layer maps them to status codes automatically.
- **Metrics** — Micrometer + Prometheus. Scrape endpoint at `/metrics` (server: `server.port`, workers: `worker.port`).
- **New feature modules** — mirror the `analytics` module structure, create a Guice module, and register it in `Starter.java`.
