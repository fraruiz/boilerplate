---
name: java-architecture-reviewer
description: Review Java architecture implementation for proper patterns and best practices. Use when user asks "check architecture", "apply good practice on architecture", or when reviewing or implementing architectural patterns.
---

# Java Architecture Reviewer

## When to use

Use this skill when:
- Reviewing overall system architecture for quality and best practices
- Implementing new modules or features following architectural patterns
- Refactoring existing architectural code
- Identifying architectural violations or anti-patterns
- Setting up architectural foundations for new projects
- Validating adherence to Domain-Driven Design principles
- Reviewing separation of concerns and layer boundaries

## Requirements
- Use `/java-testing-reviewer` for testing architecture patterns and implementation
- Use `/java-cqrs-reviewer` for CQRS architecture patterns and implementation  

## Pipeline

### 1. Architecture Overview

The project follows a clean architecture approach with Domain-Driven Design (DDD) principles, implementing CQRS with Event Sourcing patterns:

#### Core Architectural Principles
- **Domain-Centric Design**: Business logic lives in the domain layer
- **Clean Architecture**: Clear separation between layers and concerns
- **Dependency Inversion**: Dependencies point inward toward the domain
- **CQRS Pattern**: Separate read and write models
- **Event-Driven Architecture**: Domain events for loose coupling
- **Hexagonal Architecture**: Ports and adapters pattern

#### Layer Structure
```
app/                    # Application entry point (controllers, configuration)
├── main/
│   ├── com/example/app/
│   │   ├── Starter.java          # Spring Boot application
│   │   ├── iam/                  # Module controllers
│   │   └── analytics/             # Module controllers
└── test/

src/                    # Business logic and infrastructure
├── shared/              # Shared domain and infrastructure
│   ├── main/
│   │   ├── domain/      # Shared domain primitives
│   │   └── infrastructure/ # Shared infrastructure
└── iam/                 # Business modules
    ├── main/
    │   ├── domain/      # Domain layer (entities, value objects, services)
    │   ├── application/ # Application layer (use cases, commands, queries)
    │   └── infrastructure/ # Infrastructure layer (repositories, external services)
    └── test/             # Module tests
```

### 2. Domain Layer Architecture

#### Core Domain Primitives

##### Entity Pattern
```java
public abstract class Entity<ID extends Identifier> {
    protected final ID id;
    protected final CreatedAt dateTime;
    protected UpdatedAt updatedAt;
    
    // Identity-based equality
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Entity<?> entity = (Entity<?>) o;
        return Objects.equals(id, entity.id);
    }
}
```

##### Aggregate Pattern
```java
public abstract class Aggregate<ID extends Identifier> extends Entity<ID> {
    private List<Event> domainEvents = new ArrayList<>();
    
    // Domain event management
    final public Event[] pullDomainEvents() {
        List<Event> events = domainEvents;
        domainEvents = Collections.emptyList();
        return events.toArray(new Event[]{});
    }
    
    final protected void record(Event event) {
        domainEvents.add(event);
    }
}
```

##### Value Object Pattern
```java
public abstract class StringValueObject implements Serializable {
    private final String value;
    
    protected void ensureValueIsNotBlank(String errorMessage) {
        if (this.value.isBlank()) {
            throw new InvalidArgumentError(errorMessage);
        }
    }
    
    // Value-based equality
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        StringValueObject that = (StringValueObject) o;
        return Objects.equals(value, that.value);
    }
}
```

#### Domain Entity Structure

```java
public final class User extends Aggregate<UserId> {
    private Username username;
    private Email email;
    private UserFirstname firstname;
    private UserLastname lastname;
    private UserStatus status;
    
    // Factory method for creation
    public static User create(UserId id, 
                              Username username, 
                              Email email, 
                              UserFirstname firstname, 
                              UserLastname lastname,
                              CreatedAt dateTime, 
                              UpdatedAt updatedAt) {
        User user = new User(id, username, email, firstname, lastname, 
                           UserStatus.ACTIVE, dateTime, updatedAt);
        user.record(UserCreated.from(user));
        return user;
    }
    
    // Business methods
    public void lock(UpdatedAt updatedAt) {
        if (this.status == UserStatus.LOCKED) {
            throw new UserAlreadyLocked(this.id);
        }
        this.status = UserStatus.LOCKED;
        this.updatedAt = updatedAt;
        this.record(UserLocked.from(this));
    }
}
```

#### Domain Services Pattern
Domain services represent a **grouping of business logic that we can reuse** across multiple Application Services.

Let's look at an example to better explain this. We have two use cases in our application:

- Retrieve a video based on its identifier
- Modify the title of a specific video

In both use cases, we will need the business logic to:

- Go to the video repository to find a specific video based on its identifier
- Throw a domain exception of type VideoNotFound if the video is not found. It's important to note that, as we discussed in the video, the repository implementation is not the one that throws this exception.

- Return the video if it is found

To avoid duplicating this business logic in the two Application Services, we typically extract it into a Domain Service that we will invoke from both use cases.

It is important to note that domain services **will never publish domain events** or manage transactions. We leave that to the Application Service that invokes us to avoid duplication, since it is really the one that establishes the "atomicity" of the use case.

```java
@Component
public final class AuthenticateFactory {
    private final AuthenticationFactory factory;
    private final AuthenticationRepository repository;
    private final TransactionManager transactionManager;
    private final EventBus eventBus;
    
    public Authentication execute(AuthenticationId id, 
                                  AuthenticationIdentifier identifier, 
                                  RawCredential password) {
        Authentication auth = factory.execute(id, identifier, password);
        
        repository.save(auth);
        eventBus.publish(auth.pullDomainEvents());
        return auth;
    }
}
```

#### Domain Events Pattern
```java
public abstract class Event {
    private final String aggregateId;
    private final String eventId;
    private final OffsetDateTime occurredOn;
    
    public abstract String event();
    public abstract Map<String, Serializable> toPrimitives();
}

public final class UserCreated extends Event {
    public static UserCreated from(User user) {
        return new UserCreated(user.id().value()) {
            @Override
            public Map<String, Serializable> toPrimitives() {
                return Map.of(
                    "id", user.id().value(),
                    "username", user.username().value(),
                    "email", user.email().value()
                );
            }
        };
    }
}
```

### 3. Application Layer Architecture

#### Application Services
An application service is:
- These are the **entry points to our application**. That is, as seen in the diagram, command-line controllers or our [HTTP API (like the one in the Scala course!)](https://pro.codely.tv/library/api-http-con-scala-y-akka/about/) will invoke the application services.
- They **represent an atomically defined use case of our system**. In case of modifications to the state of our application:
- They can perform **transactional barriers** with the persistence system.
- They will publish the respective domain events.
- They coordinate the calls to the different elements of our system to execute a specific use case.
- We will refer to them interchangeably as Application Service and Use Case.
- Avoid modifying different entities
- Apply SOLID principles

```java
@Component
public final class UserByIdFinder {
    private final UserFinder finder;
    
    public UserResponse execute(UserId id) {
        User user = finder.execute(id);
        
        return UserResponse.map(user);
    }
}
```

```java
import java.time.OffsetDateTime;

@Component
public final class UserRegistrar {
    private final UserRepository finder;
    private final TransactionManager transactionManager;
    private final EventBus eventBus;
    private final DateProvider dateProvider;

    public UserRegistrar(UserRepository finder,
                         TransactionManager transactionManager,
                         EventBus eventBus,
                         DateProvider dateProvider) {
        this.finder = finder;
        this.transactionManager = transactionManager;
        this.eventBus = eventBus;
        this.dateProvider = dateProvider;
    }

    public void execute(UserId id, Username username) {
        this.ensureUserNotExists(id);

        OffsetDateTime now = this.dateProvider.getOffsetDateTimeNow();

        User user = User.create(id, username, now);

        transactionManager.initAndExecute(() -> {
            this.repository.save(user);
            this.eventBus.publish(user.pullDomainEvents());    
        });
    }

    public void ensureUserNotExists(UserId id) {
        finder.execute(id);
    }
}
```

:danger: Warning 

Error
```java
@Component
public final class UserRegistrar {
    private final UserRepository finder;
    private final EventSender sender;
    private final TransactionManager transactionManager;
    private final EventBus eventBus;
    private final DateProvider dateProvider;

    public UserRegistrar(UserRepository finder,
                         EventSender sender,
                         TransactionManager transactionManager,
                         EventBus eventBus,
                         DateProvider dateProvider) {
        this.finder = finder;
        this.sender = sender;
        this.transactionManager = transactionManager;
        this.eventBus = eventBus;
        this.dateProvider = dateProvider;
    }

    public void execute(UserId id, Username username) {
        this.ensureUserNotExists(id);

        OffsetDateTime now = this.dateProvider.getOffsetDateTimeNow();

        User user = User.create(id, username, now);

        transactionManager.initAndExecute(() -> {
            this.repository.save(user);
            this.sender.send(WelcomeEmail.build(use));
        });
    }

    public void ensureUserNotExists(UserId id) {
        finder.execute(id);
    }
}
```

Apply SOLID principles and event driven architecture
```java
@Component
public final class UserRegistrar {
    private final UserRepository finder;
    private final EventSender sender;
    private final TransactionManager transactionManager;
    private final EventBus eventBus;
    private final DateProvider dateProvider;

    public UserRegistrar(UserRepository finder,
                         EventSender sender,
                         TransactionManager transactionManager,
                         EventBus eventBus,
                         DateProvider dateProvider) {
        this.finder = finder;
        this.sender = sender;
        this.transactionManager = transactionManager;
        this.eventBus = eventBus;
        this.dateProvider = dateProvider;
    }

    public void execute(UserId id, Username username) {
        this.ensureUserNotExists(id);

        OffsetDateTime now = this.dateProvider.getOffsetDateTimeNow();

        User user = User.create(id, username, now);

        transactionManager.initAndExecute(() -> {
            this.repository.save(user);
            this.eventBus.publish(user.pullDomainEvents());
        });
    }

    public void ensureUserNotExists(UserId id) {
        finder.execute(id);
    }
}

public final class SendWelcomeEmailOnUserCreated implements EventSubscriber<UserCreated> {
    private final EmailSender sender;
    
    public SendWelcomeEmailOnUserCreated(EmailSender sender) {
        this.sender = sender;
    }
    
    @Override
    public void on(UserCreated event) {
        UserId userId = new UserId(event.id());
        
        sender.execute(userId);
    }
}
```

#### CQRS Implementation
Use `/java-cqrs-reviewer` skill

### 4. Infrastructure Layer Architecture

#### Repository Pattern
```java
// Domain contract
public interface UserRepository extends Repository<User, UserCriteria> {
}

// Infrastructure implementation
@Component
public final class PostgresUserRepository extends PostgresSqlRepository<User> implements UserRepository {
    public PostgresUserRepository(@Qualifier("iam-jdbc_template") JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }
    
    @Override
    public Pagination<User> match(UserCriteria criteria) {
        Table table = new Table(table(), 'u');
        Columns columns = new Columns(columns());
        
        List<Expression> expressions = new ArrayList<>();
        criteria.username().ifPresent(x -> expressions.add(Expression.equals("USERNAME")));
        criteria.email().ifPresent(x -> expressions.add(Expression.equals("EMAIL")));
        
        CriteriaBuilder builder = CriteriaBuilder.from(table, columns).where(Predicate.from(expressions));
        return super.match(criteria, builder);
    }
}
```

#### Generic Repository Base
```java
@Component
public abstract class PostgresSqlRepository<T> implements RowMapper<T> {
    private final JdbcTemplate jdbcTemplate;
    
    public void save(Identifier identifier, Serializable... values) {
        if (this.isInsertStatement(identifier)) {
            this.insert(identifier, values);
        } else {
            this.update(identifier, values);
        }
    }
    
    protected abstract String table();
    protected abstract String columns();
    protected abstract T mapRow(ResultSet rs, int rowNum) throws SQLException;
}
```

#### Bus Implementation
```java
@Component
public final class InMemoryQueryBus implements QueryBus {
    private final QueryHandlersInformation information;
    private final ApplicationContext iocContainer;
    
    @Override
    public Response ask(Query query) throws QueryHandlerExecutionError {
        try {
            Class<? extends QueryHandler> queryHandlerClass = information.search(query.getClass());
            QueryHandler handler = iocContainer.getBean(queryHandlerClass);
            return handler.handle(query);
        } catch (Throwable error) {
            throw new QueryHandlerExecutionError(error);
        }
    }
}
```

### 5. Module Organization Pattern

#### Module Structure
```
src/module/main/com/example/module/
├── domain/
│   ├── [Entity].java                     # Domain aggregate
│   ├── [Entity]Repository.java            # Repository interface
│   ├── [Entity]Criteria.java              # Search criteria
│   ├── valueobjects/                     # Value objects
│   ├── events/                           # Domain events
│   ├── services/                         # Domain services
│   └── erros/                           # Domain exceptions
├── application/
│   ├── [Entity]Response.java              # Response DTOs
│   ├── command/
│   │   ├── [Action][Entity]Command.java
│   │   └── [Action][Entity]CommandHandler.java
│   ├── query/
│   │   ├── [Action][Entity]Query.java
│   │   └── [Action][Entity]QueryHandler.java
│   └── [Entity][Action]r.java            # Application services
└── infrastructure/
    └── persistence/
        └── Postgres[Entity]Repository.java
```

#### Shared Module Structure
```
src/shared/main/com/example/shared/
├── domain/
│   ├── Aggregate.java                    # Base aggregate
│   ├── Entity.java                       # Base entity
│   ├── Component.java                    # Component annotation
│   ├── valueobjects/                     # Shared value objects
│   ├── repositories/                     # Repository interfaces
│   ├── bus/                             # Message bus interfaces
│   └── errors/                          # Shared domain errors
└── infrastructure/
    ├── persistence/
    │   └── postgresql/                   # PostgreSQL base classes
    ├── bus/                             # Bus implementations
    └── [Other infrastructure components]
```

### 6. Application Entry Point

#### Spring Boot Configuration
```java
@SpringBootApplication(exclude = {
    DataSourceAutoConfiguration.class,
    SecurityAutoConfiguration.class,
    // Other exclusions
})
@ComponentScan(
    includeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Component.class),
    value = {"com.example.shared", "com.example.iam", "com.example.analytics", "com.example.app"}
)
public class Starter {
    static void main() {
        SpringApplication.run(Starter.class);
    }
}
```

#### Controller Pattern
```java
@RestController
public class UsersController {
    private final CommandBus commandBus;
    private final QueryBus queryBus;
    
    @PostMapping("/users")
    public ResponseEntity<Map<String, String>> createUser(@RequestBody CreateUserRequest request) {
        CreateUserCommand command = new CreateUserCommand(
            request.id(), request.username(), request.email(), 
            request.firstname(), request.lastname()
        );
        commandBus.dispatch(command);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/users/{id}")
    public ResponseEntity<UserResponse> findUser(@PathVariable String id) {
        FindUserQuery query = new FindUserQuery(id);
        UserResponse user = queryBus.ask(query);
        return ResponseEntity.ok(user);
    }
}
```

### 7. Architectural Patterns and Best Practices

#### Dependency Inversion
- **Domain Layer**: No external dependencies
- **Application Layer**: Depends on domain interfaces
- **Infrastructure Layer**: Implements domain contracts
- **App Layer**: Orchestrates everything together

#### Bounded Contexts
- **IAM Context**: Identity and Access Management
- **Analytics Context**: Event analytics and reporting
- **Shared Context**: Common domain primitives

#### Aggregate Design
- **Single Aggregate Root**: One aggregate per transaction
- **Consistency Boundaries**: Aggregates maintain consistency
- **Event Publishing**: Aggregates publish domain events
- **Business Invariants**: Encapsulated within aggregates

#### Repository Design
- **Interface in Domain**: Repository contracts in domain layer
- **Implementation in Infrastructure**: Concrete implementations
- **Criteria Pattern**: Flexible search capabilities
- **Pagination Support**: Built-in pagination for list queries

### 8. Anti-Patterns to Avoid

#### Domain Layer Anti-Patterns
- **Anemic Domain Models**: Entities without behavior
- **Infrastructure in Domain**: No external dependencies in domain
- **Primitive Obsession**: Use value objects instead of primitives
- **God Aggregates**: Keep aggregates small and focused
- **Leaky Abstractions**: Don't expose internal state

#### Application Layer Anti-Patterns
- **Business Logic in Handlers**: Delegate to domain services
- **Direct Infrastructure Access**: Use repositories and services
- **Transaction Script**: Use domain objects, not procedural code
- **Fat Controllers**: Keep controllers thin
- **Mixed Responsibilities**: Clear separation of concerns

#### Infrastructure Layer Anti-Patterns
- **Domain Dependencies**: Infrastructure shouldn't depend on domain
- **Concrete Dependencies**: Depend on abstractions
- **SQL in Application**: Keep SQL in repositories
- **Missing Abstractions**: Use interfaces for all external systems
- **Hard-coded Configuration**: Externalize configuration

#### General Anti-Patterns
- **Circular Dependencies**: Avoid circular references
- **Tight Coupling**: Use dependency injection
- **Missing Error Handling**: Handle exceptions appropriately
- **Inconsistent Naming**: Follow established conventions
- **Violation of Layer Boundaries**: Respect architectural layers

### 9. Quality Checklist

#### Domain Layer Quality
- [ ] Entities extend proper base classes
- [ ] Aggregates manage domain events
- [ ] Value objects are immutable
- [ ] Domain services contain business logic
- [ ] No external dependencies in domain
- [ ] Proper exception handling with domain errors

#### Application Layer Quality
- [ ] Commands/queries are immutable records
- [ ] Handlers delegate to domain services
- [ ] Response DTOs contain only necessary data
- [ ] Proper use of CQRS pattern
- [ ] Application services coordinate use cases
- [ ] No business logic in handlers

#### Infrastructure Layer Quality
- [ ] Repositories implement domain interfaces
- [ ] Infrastructure has no domain dependencies
- [ ] Proper use of dependency injection
- [ ] External systems are abstracted
- [ ] Configuration is externalized
- [ ] Error handling is appropriate

#### Module Organization Quality
- [ ] Clear module boundaries
- [ ] Proper package structure
- [ ] Consistent naming conventions
- [ ] Minimal cross-module dependencies
- [ ] Shared code in shared module
- [ ] Module-specific code isolated

#### Architecture Quality
- [ ] Dependency inversion principle followed
- [ ] Clean architecture layers respected
- [ ] Bounded contexts are well-defined
- [ ] Event-driven communication
- [ ] Proper separation of concerns
- [ ] Scalable and maintainable structure

### 10. Architectural Decision Records

#### Key Architectural Decisions

1. **Clean Architecture with DDD**: Chosen for maintainability and business alignment
2. **CQRS Pattern**: Separates read/write concerns for scalability
3. **Event-Driven Architecture**: Enables loose coupling and eventual consistency
4. **Hexagonal Architecture**: Ports and adapters for infrastructure independence
5. **Module Monolith**: Organized by bounded contexts within single deployment

#### Technology Stack
- **Spring Boot**: Application framework and dependency injection
- **PostgreSQL**: Primary data store
- **JDBC**: Database access for performance and control
- **Java Records**: Immutable data structures
- **Maven/Gradle**: Build and dependency management

This architectural approach ensures maintainable, scalable, and business-aligned code that can evolve with changing requirements while maintaining high quality and consistency across the system.