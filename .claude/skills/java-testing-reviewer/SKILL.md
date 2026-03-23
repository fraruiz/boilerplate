---
name: java-testing-reviewer
description: Review Java testing code for avoid bugs. Use when user asks "check tests", "apply good practice on tests", or when reviewing or doing testing code.
---

# Java Testing Reviewer

## When to use

Use this skill when:
- Reviewing test code for quality and best practices
- Writing new tests and want to follow established patterns
- Refactoring existing tests
- Identifying testing anti-patterns or bugs
- Setting up testing infrastructure for new modules

## Pipeline

### 1. Test Architecture Overview

The project follows a layered testing architecture with clear separation of concerns:

#### Base Test Classes
- **UnitTestCase**: Base class for unit tests providing common mocks (EventBus, DateProvider, etc.)
- **InfrastructureTestCase**: Base class for integration tests with database setup
- **Context Test Cases**: Module-specific base classes (e.g., `IdentityAccessManagementContextUnitTestCase`).

#### Module Test Cases
- **ModuleUnitTestCase**: Module-specific unit test base (e.g., `AuthenticationsModuleUnitTestCase`) that extend ContextUnitTestCase. This TestCase contains common mocks as repository (or others ports) of module. All's unit test of module cases should extend of this test case. 
- **ModuleInfrastructureTestCase**: Module-specific integration test base that extend ContextInfrastructureTestCase. This TestCase contains common dependencies as repository implementations. All's integrations tests should extend of this test case.

### 2. Object Mothers Pattern

Object Mothers are factory classes that create test data instances with meaningful defaults and customization options.

#### Structure
```java
public final class EntityMother extends AggregateMother<Entity, EntityId> {
    // Static factory methods for common scenarios
    public static EntityMother random() { ... }
    public static EntityMother withSpecificState() { ... }
    
    // Builder pattern for customization
    public EntityMother withProperty(Property value) { ... }
    
    // Build method to create the actual entity
    @Override
    public Entity build() { ... }
}
```

#### Best Practices
- **Inheritance Hierarchy**: `EntityMother` → `AggregateMother` → `SpecificMother`
- **Factory Methods**: Use descriptive static methods (`random()`, `succeeded()`, `failed()`, `locked()`)
- **Fluent Interface**: Use `withXxx()` methods for customization
- **Immutable Objects**: Return new instances rather than modifying existing ones
- **Meaningful Defaults**: Provide realistic default values for all properties

#### Examples
```java
// Basic usage
User user = UserMother.random().build();

// Specific scenarios
User lockedUser = UserMother.locked().build();
User inactiveUser = UserMother.inactive().build();

// Customization
User customUser = UserMother.random()
    .withEmail("test@example.com")
    .withStatus(UserStatus.ACTIVE)
    .build();
```

### 3. Test Organization Patterns

#### Module Structure
```
src/module/test/com/example/module/
├── ModuleUnitTestCase.java          # Base unit test class
├── ModuleInfrastructureTestCase.java # Base integration test class
├── domain/
│   ├── EntityMother.java           # Object mother for domain entities
│   ├── services/
│   │   └── ServiceTest.java        # Domain service tests
├── application/
│   ├── command/
│   │   └── CommandHandlerTest.java # Application command tests
│   └── query/
│       └── QueryHandlerTest.java   # Application query tests
└── infrastructure/
    └── persistence/
        └── RepositoryTest.java      # Repository integration tests
```

#### Naming Conventions
- Test classes: `[ClassName]Test`
- Object mothers: `[ClassName]Mother`
- Value object mothers: `[ValueObjectName]Mother`
- Base classes: `[Module]ModuleUnitTestCase`, `[Module]ModuleInfrastructureTestCase`

### 4. Testing Types

#### 4.1. Unit Tests

**Purpose**: Test individual components in isolation
**Characteristics**:
- Extend `UnitTestCase` or module-specific unit test base
- Use mocks for all dependencies
- Fast execution, no external dependencies
- Focus on business logic

**Structure**:
```java
public final class ServiceTest extends ModuleUnitTestCase {
    private Service service;
    
    private Dependency otherDependency;
    
    @BeforeEach
    public void setUp() {
        super.setUp();
        this.service = new Service(super.dependency, this.otherDependency);
    }
    
    @Test
    void should_do_something_when_condition_met() {
        Entity entity = EntityMother.random().build();

        super.shouldMockDependency(entity);
        this.shouldOtherMockDependency(entity);
        
        Result result = service.execute(entity);
        
        assertEquals(expected, result);
        super.shouldHavePublished(expectedEvent);
    }
    
    private void shouldOtherMockDependency(Entity entity) {
        when(this.otherDependency.method()).thenReturn(entity);
    }
}
```

**Best Practices**:
- Use descriptive test method names following `should_when` pattern
- Arrange-Act-Assert structure
- Use Object Mothers for test data
- Verify mocks using `shouldXxx()` helper methods
- Test both happy path and edge cases
- If contains dependencies of other modules, can mock in the test. 
- Always use test case methods for mocking. If such a method does not exist, create a method that specifies 'shouldXxx'.
- Never use any() method for mocking.

#### 4.2. Infrastructure Tests

**Purpose**: Test integration with external systems (databases, APIs)
**Characteristics**:
- Extend `InfrastructureTestCase` or module-specific infrastructure test base
- Use real database (test containers or embedded)
- Slower execution, real dependencies
- Focus on persistence and integration

**Structure**:
```java
public final class RepositoryTest extends ModuleInfrastructureTestCase {
    private Repository repository;
    
    @BeforeEach
    @Override
    public void setUp() {
        super.setUp();
        this.repository = new Repository(jdbcTemplate);
    }
    
    @Test
    void should_save_entity() {
        Entity expected = EntityMother.random().build();
        
        repository.save(expected);
        
        Entity actual = super.find(expected.id());
        assertEquals(expected, actual);
    }
    
    @Test
    void should_find_by_criteria() {
        Entity preset = EntityMother.random().build();
        super.insert(preset);
        
        Criteria criteria = Criteria.byProperty(preset.property());
        List<Entity> results = repository.match(criteria);
        
        assertEquals(1, results.size());
        assertEquals(preset, results.get(0));
    }
}
```

**Best Practices**:
- Use database schema setup in base class
- Clean up data between tests (`@AfterEach`)
- Test CRUD operations thoroughly
- Use real test data, not mocks
- Verify database constraints and behavior

#### 4.3. Application Tests
WIP

#### 4.4. Acceptance Tests
WIP

### 5. Common Testing Patterns

#### Mock Verification Patterns
```java
// Repository interactions
public void shouldSave(Entity entity) {
    verify(repository, atLeastOnce()).save(entity);
}

// Event publishing
public void shouldHavePublished(Event... events) {
    verify(eventBus, atLeastOnce()).publish(events);
}

// Specific event types
public void shouldHavePublished(Class<? extends Event> eventClass) {
    verify(eventBus, atLeastOnce()).publish(any(eventClass));
}
```

#### Data Setup Patterns
```java
// Using Object Mothers
Entity entity = EntityMother.random().build();
Entity specificEntity = EntityMother.succeeded().build();

// Customizing entities
Entity customEntity = EntityMother.random()
    .withStatus(Status.ACTIVE)
    .withProperty("value")
    .build();
```

#### Assertion Patterns
```java
// Entity comparison
assertEquals(expected, actual);

// Collection assertions
assertEquals(expected, actual);

// Optional assertions
assertTrue(optional.isPresent());
assertEquals(expected, optional.get());

// Exception assertions
assertThrows(ExpectedException.class, () -> riskyOperation());
```

### 6. Anti-Patterns to Avoid

#### Test Code Smells
- **Test Logic in Production**: Don't put test-specific code in production classes
- **Hard-coded Test Data**: Use Object Mothers instead of hardcoded values
- **Multiple Assertions**: Focus on one behavior per test
- **Implementation Testing**: Test behavior, not implementation details
- **Shared State**: Ensure tests are independent and isolated
- **Use any()**: Ensure don't use any() method in mocks.

#### Common Mistakes
- **Missing Mock Setup**: Forgetting to configure mock behavior
- **Incomplete Verification**: Not verifying all important interactions
- **Test Pollution**: Tests that leave side effects
- **Over-mocking**: Mocking too much, testing nothing
- **Brittle Tests**: Tests that break for unrelated changes

### 7. Quality Checklist

#### Test Structure
- [ ] Test class extends appropriate base class
- [ ] Descriptive test method names
- [ ] Arrange-Act-Assert structure
- [ ] Single responsibility per test
- [ ] Proper setup and teardown

#### Test Data
- [ ] Uses Object Mothers for test data
- [ ] Meaningful default values
- [ ] Proper customization when needed
- [ ] No hardcoded magic values

#### Mocking
- [ ] All dependencies properly mocked
- [ ] Mock behavior configured before test execution
- [ ] Important interactions verified
- [ ] No over-mocking of trivial dependencies

#### Assertions
- [ ] Clear, focused assertions
- [ ] Testing behavior, not implementation
- [ ] Proper exception testing
- [ ] Edge cases covered

#### Infrastructure
- [ ] Database cleanup between tests
- [ ] Proper schema setup
- [ ] Transaction management
- [ ] Isolation from other tests

### 8. Tools and Frameworks

- **JUnit 5**: Testing framework
- **Mockito**: Mocking framework
- **Spring Boot Test**: For integration tests
- **TestContainers**: For database integration testing
- **AssertJ**: Fluent assertions (if available)

This comprehensive testing approach ensures maintainable, reliable tests that provide confidence in code quality while supporting rapid development and refactoring.

