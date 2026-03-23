---
name: java-cqrs-reviewer
description: Review Java CQRS implementation for proper patterns and best practices. Use when user asks "check CQRS", "apply good practice on CQRS", or when reviewing or implementing CQRS patterns.
---

# Java CQRS Reviewer

## When to use

Use this skill when:
- Reviewing CQRS implementation for quality and best practices
- Implementing new commands, queries, or handlers
- Refactoring existing CQRS code
- Identifying CQRS anti-patterns or architectural violations
- Setting up CQRS infrastructure for new modules
- Validating separation between read and write models

## Pipeline

### 1. CQRS Architecture Overview

CQRS (Command Query Responsibility Segregation) separates the read and write operations of a system into distinct models:

#### Core Components
- **Commands**: Intent to change state (write operations)
- **Queries**: Intent to retrieve data (read operations)
- **Handlers**: Process commands and queries
- **Buses**: Route commands/queries to appropriate handlers
- **Responses**: Data returned from queries

#### Base Interfaces
```java
// Commands - Write Operations
public interface Command {}
public interface CommandHandler<T extends Command> {
    void handle(T command);
}

// Queries - Read Operations  
public interface Query {}
public interface QueryHandler<Q extends Query, R extends Response> {
    R handle(Q query);
}

// Responses - Query Results
public interface Response {}
```

### 2. Command Pattern Implementation

#### Structure
Commands are immutable data structures that represent user intentions:

```java
public record AuthenticateCommand(String authenticationId, String identifier, String password) implements Command {
}
```

#### Best Practices
- **Immutable Records**: Use Java records for immutability
- **Primitive Types**: Keep commands simple with primitive types
- **Validation**: Validate in handlers, not in commands
- **Single Responsibility**: Each command represents one action
- **Descriptive Names**: Use verb-noun pattern (e.g., `CreateUserCommand`, `AuthenticateCommand`)

#### Command Handlers
```java
@Component
public final class AuthenticateCommandHandler implements CommandHandler<AuthenticateCommand> {
    private final Authenticator authenticator;

    public AuthenticateCommandHandler(Authenticator authenticator) {
        this.authenticator = authenticator;
    }

    @Override
    public void handle(AuthenticateCommand command) {
        // Convert command to domain objects
        AuthenticationId id = new AuthenticationId(command.authenticationId());
        AuthenticationIdentifier identifier = new AuthenticationIdentifier(command.identifier());
        RawCredential password = new RawCredential(command.password());

        // Execute business logic
        this.authenticator.execute(id, identifier, password);
    }
}
```

#### Handler Best Practices
- **@Component Annotation**: Register with Spring container
- **Dependency Injection**: Inject domain services or repositories
- **Object Conversion**: Convert command primitives to domain objects
- **Business Logic Delegation**: Delegate to domain services
- **No Return Value**: Commands don't return values (void return)

### 3. Query Pattern Implementation

#### 3.1. Query By filter pattern implementation
##### Structure
Queries represent data retrieval requests:

```java
public record FindUserQuery(String id) implements Query {
}
```

##### Response Objects
Queries return structured response objects:

```java
public record UserResponse(String id,
                           String username,
                           String email,
                           String firstname,
                           String lastname,
                           String status) implements Response {
    public static UserResponse map(User user) {
        return new UserResponse(
                user.id().value(),
                user.username().value(),
                user.email().value(),
                user.firstname().value(),
                user.lastname().value(),
                user.status().name().toLowerCase()
        );
    }
}
```

##### Query Handlers
```java
@Component
public final class FindUserQueryHandler implements QueryHandler<FindUserQuery, UserResponse> {
    private final UserByIdFinder finder;

    public FindUserQueryHandler(UserByIdFinder finder) {
        this.finder = finder;
    }

    @Override
    public UserResponse handle(FindUserQuery query) {
        UserId userId = new UserId(query.id());
        
        return finder.execute(userId);
    }
}
```
#### 3.2. Query By criteria pattern implementation
Queries represent data retrieval requests:

```java
public record SearchCoursesByCriteriaQuery(Optional<String> name, 
                                           Integer size, 
                                           Integer page) implements Query { }
```

##### Response Objects
Queries return structured response objects:
```java
public record CourseResponse(String id, String name, String duration) implements Response {
    public static CourseResponse map(Course course) {
        return new CourseResponse(course.id().value(), course.name().value(), course.duration().value());
    }
}
```

##### Query Handlers
```java
@Component
public final class SearchCoursesByCriteriaQueryHandler implements QueryHandler<SearchCoursesByCriteriaQuery, Pagination<CourseResponse>> {
    private final BackofficeCoursesByCriteriaSearcher searcher;

    public SearchBackofficeCoursesByCriteriaQueryHandler(BackofficeCoursesByCriteriaSearcher searcher) {
        this.searcher = searcher;
    }

    @Override
    public Pagination<CourseResponse> handle(SearchCoursesByCriteriaQuery query) {
        CourseCriteria criteria = CourseCriteria.from(query.name(), query.size(), query.page()) ;

        return searcher.search(criteria);
    }
}
```

##### Query Best Practices
- **Return Responses**: Always return typed response objects
- **Mapping Methods**: Use static `map()` methods for domain-to-response conversion
- **Search Services**: Delegate to dedicated search/find services
- **No Side Effects**: Queries should never modify state
- **Pagination Support**: Use `Pagination<T>` for list queries

### 4. Bus Implementation Pattern

#### Command Bus
```java
public interface CommandBus {
    void dispatch(Command command) throws CommandHandlerExecutionError;
}
```

#### Query Bus
```java
public interface QueryBus {
    <R> R ask(Query query) throws QueryHandlerExecutionError;
}
```

#### In-Memory Implementation
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

### 5. Controller Integration Pattern

Controllers should be thin layers that coordinate between HTTP and CQRS:

```java
@RestController
public class AuthenticationsController {
    private final CommandBus commandBus;
    private final IdentifierGenerator identifierGenerator;
    private final TokenGenerator tokenProvider;

    @PostMapping("/users")
    public ResponseEntity<Map<String, String>> authenticate(@RequestBody Request request) {
        CreateUserCommand command = new CreateUserCommand(command.id(), 
                                                          command.username(), 
                                                          command.email(), 
                                                          command.firstname(), 
                                                          command.lastname());
        
        commandBus.dispatch(command);
        
        return ResponseEntity.ok().build();
    }
    
    record Request(String id, String username, String email, String firstname, String lastname) {}
}
```

#### Controller Best Practices
- **Thin Controllers**: Minimal logic, mostly coordination
- **Command Creation**: Convert HTTP requests to commands
- **Bus Dispatching**: Use command/query buses for processing
- **Response Building**: Create HTTP responses from command results
- **Error Handling**: Handle exceptions appropriately

### 6. Module Organization

#### Directory Structure
```
src/module/main/com/example/module/
├── application/
│   ├── [Entity]Response.java          # Response DTOs
│   ├── command/
│   │   ├── [Action][Entity]Command.java
│   │   └── [Action][Entity]CommandHandler.java
│   └── query/
│       ├── [Action][Entity]Query.java
│       └── [Action][Entity]QueryHandler.java
└── domain/
    ├── application/
    │   ├── [Entity][Action]r.java      # Domain services
    │   └── [Entity][Action]or.java     # Domain services
    └── [Entity].java                   # Domain entities
```

#### Naming Conventions
- **Commands**: `[Action][Entity]Command` (e.g., `CreateUserCommand`, `AuthenticateCommand`)
- **Command Handlers**: `[Action][Entity]CommandHandler`
- **Queries**: `[Action][Entity]Query` (e.g., `FindUserQuery`, `SearchUsersByCriteriaQuery`)
- **Query Handlers**: `[Action][Entity]QueryHandler`
- **Responses**: `[Entity]Response` (e.g., `UserResponse`, `AuthenticationResponse`)
- **Services**: `[Entity][Action]r` or `[Entity][Action]or` (e.g., `UserCreator`, `Authenticator`)

### 7. Common CQRS Patterns

#### Command Patterns
```java
// Create commands
public record CreateUserCommand(String username, String email, String password) implements Command {}

// Update commands  
public record UpdateUserCommand(String id, String username, String email) implements Command {}

// Delete commands
public record RemoveUserCommand(String id) implements Command {}

// Action commands
public record AuthenticateCommand(String authenticationId, String identifier, String password) implements Command {}
```

#### Query Patterns
```java
// Find by ID
public record FindUserQuery(String id) implements Query {}

// Search by criteria
public record SearchUsersByCriteriaQuery(String criteria, int page, int size) implements Query {}

// List all
public record ListUsersQuery(int page, int size) implements Query {}
```

#### Response Patterns
Single entity response
```java
public record UserResponse(String id, String username, String email) implements Response {
    public static UserResponse map(User user) { ... }
}
```

List entities response: User `Pagination<UserResponse>`


### 8. Anti-Patterns to Avoid

#### Command Anti-Patterns
- **Commands with Logic**: Commands should be data containers only
- **Commands Returning Values**: Commands should have void return type
- **Multiple Responsibilities**: One command should do one thing
- **Mutable Commands**: Commands must be immutable
- **Complex Validation**: Validation belongs in handlers or domain

#### Query Anti-Patterns
- **Queries Modifying State**: Queries must be read-only
- **Complex Queries**: Break down complex queries into smaller ones
- **Direct Database Access**: Use repositories or search services
- **Returning Domain Objects**: Always return response DTOs
- **Missing Pagination**: List queries should support pagination

#### Handler Anti-Patterns
- **Business Logic in Handlers**: Delegate to domain services
- **Multiple Responsibilities**: Handlers should coordinate only
- **Direct Infrastructure Access**: Use repositories and services
- **Missing Error Handling**: Handle exceptions appropriately
- **Circular Dependencies**: Avoid circular dependency injection

#### General Anti-Patterns
- **Mixing Commands and Queries**: Keep them separate
- **Shared Models**: Don't use same models for read/write
- **Ignoring Eventual Consistency**: Plan for consistency delays
- **Complex Controllers**: Keep controllers thin
- **Missing Transactions**: Commands should be transactional

### 9. Quality Checklist

#### Command Quality
- [ ] Command implements `Command` interface
- [ ] Uses immutable record structure
- [ ] Contains only primitive or value object fields
- [ ] Has descriptive verb-noun naming
- [ ] Represents single user intention

#### Command Handler Quality
- [ ] Implements `CommandHandler<T>` interface
- [ ] Annotated with `@Component`
- [ ] Has void return type
- [ ] Converts command to domain objects
- [ ] Delegates to domain services
- [ ] Handles errors appropriately

#### Query Quality
- [ ] Query implements `Query` interface
- [ ] Uses immutable record structure
- [ ] Contains search criteria only
- [ ] Has descriptive naming
- [ ] Supports pagination for list queries

#### Query Handler Quality
- [ ] Implements `QueryHandler<Q, R>` interface
- [ ] Annotated with `@Component`
- [ ] Returns typed response objects
- [ ] Uses static mapping methods
- [ ] Delegates to search/find services
- [ ] Has no side effects

#### Response Quality
- [ ] Implements `Response` interface
- [ ] Uses immutable record structure
- [ ] Contains only necessary data
- [ ] Has static `map()` methods
- [ ] Uses primitive types only

#### Integration Quality
- [ ] Controllers use command/query buses
- [ ] Thin controller implementation
- [ ] Proper error handling
- [ ] Transaction boundaries in commands
- [ ] Consistent naming conventions

### 10. Testing CQRS

#### Command Handler Testing
```java
public final class AuthenticateCommandHandlerTest extends AuthenticationsModuleUnitTestCase {
    private AuthenticateCommandHandler handler;
    
    private Authenticator authenticator;
    
    @BeforeEach
    public void setUp() {
        super.setUp();
        
        this.authenticator = mock(Authenticator.class);
        
        this.handler = new AuthenticateCommandHandler(this.authenticator);
    }
    
    @Test
    void should_handle_authentication_command() {
        Authentication authentication = AuthenticationMother.random().build();
        AuthenticateCommand command = AuthenticateCommandMother.from(authentication);
        
        handler.handle(command);

        this.shouldAuthenticate(authentication);
    }
    
    private void shouldAuthenticate(Authentication authentication) {
        verify(this.authenticator).execute(authentication.id(), authentication.identifier(), authentication.password());
    }
}
```

#### Query Handler Testing
```java
public final class FindUserQueryHandlerTest extends UsersModuleUnitTestCase {
    private FindUserQueryHandler handler;

    private UserByIdFinder finder;

    @BeforeEach
    public void setUp() {
        super.setUp();
        
        this.handler = new FindUserQueryHandler(new UserByIdFinder(super.repository));
    }
    
    @Test
    void should_return_user_response() {
        User user = UserMother.random().build();
        FindUserQuery query = FindUserQueryMother.from(user);
        UserResponse expected = UserResponse.map(user);
        
        super.shouldFind(user);
        
        UserResponse actual = handler.handle(query);
        
        assertEquals(expected, actual);
    }
}
```

This comprehensive CQRS approach ensures clean separation between read and write operations, maintainable code structure, and scalable architecture that supports complex business requirements while maintaining simplicity in individual components.