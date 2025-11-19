# Order Service - Integration Tests

This directory contains comprehensive integration tests for the Order Service microservice.

## Overview

The integration test suite provides end-to-end testing for the Order Service, covering:
- REST API endpoints
- Kafka messaging (event-driven communication)
- Database operations with PostgreSQL
- SAGA orchestration patterns
- Transactional Outbox pattern

## Test Structure

```
src/test/java/com/ct08SWA/orderservice/ordercontainer/
├── BaseIntegrationTest.java                    # Base test configuration
├── rest/
│   └── OrderControllerIntegrationTest.java    # REST API endpoint tests
├── dataaccess/
│   ├── OrderRepositoryIntegrationTest.java    # Repository layer tests
│   └── OrderOutboxIntegrationTest.java        # Outbox pattern tests
├── messaging/
│   └── OrderKafkaIntegrationTest.java         # Kafka listener tests
└── saga/
    └── OrderSagaIntegrationTest.java          # End-to-end SAGA flow tests
```

## Technologies Used

- **JUnit 5**: Test framework
- **Spring Boot Test**: Spring testing support with `@SpringBootTest`
- **MockMvc**: REST API testing without starting a full HTTP server
- **H2 Database**: In-memory database for fast testing (PostgreSQL compatibility mode)
- **Embedded Kafka**: In-memory Kafka broker for messaging tests
- **Awaitility**: Asynchronous testing utilities for event-driven tests
- **AssertJ**: Fluent assertion library

## Test Coverage

### 1. REST API Tests (`OrderControllerIntegrationTest`)

Tests the HTTP endpoints with full request/response validation:

- ✅ Create order with valid request (POST /orders)
- ✅ Create order with multiple items
- ✅ Cancel order (POST /orders/{orderId}/cancel)
- ✅ Validation errors (null fields, invalid data)
- ✅ Handle non-existent orders
- ✅ Invalid JSON request handling

**Key Features:**
- Uses MockMvc for HTTP testing
- Validates HTTP status codes (201, 400, 404, 202)
- Checks JSON response structure
- Verifies database persistence

### 2. Repository Tests (`OrderRepositoryIntegrationTest`)

Tests data access layer with H2 in-memory database:

- ✅ Save order successfully
- ✅ Find order by tracking ID
- ✅ Find order by ID
- ✅ Update order status
- ✅ Save order with multiple items
- ✅ Persist and retrieve order address
- ✅ Handle non-existent entities

**Key Features:**
- Uses H2 in PostgreSQL compatibility mode
- Tests domain-to-entity mapping
- Validates relationships (Order → OrderItems)
- Tests value object persistence

### 3. Outbox Pattern Tests (`OrderOutboxIntegrationTest`)

Tests the transactional outbox implementation:

- ✅ Save outbox message
- ✅ Find messages by status
- ✅ Update outbox status after processing
- ✅ Delete processed messages
- ✅ Handle multiple messages for same saga
- ✅ Order messages by creation time
- ✅ Persist large JSON payloads

**Key Features:**
- Validates transactional consistency
- Tests outbox status transitions
- Ensures reliable event publishing

### 4. Kafka Integration Tests (`OrderKafkaIntegrationTest`)

Tests Kafka message consumption and processing:

- ✅ Handle payment completed event
- ✅ Handle payment failed event
- ✅ Handle payment cancelled event
- ✅ Handle restaurant approved event
- ✅ Handle restaurant rejected event
- ✅ Ignore events for non-existent orders

**Key Features:**
- Uses Embedded Kafka for testing
- Tests event deserialization (Debezium format)
- Validates state transitions after events
- Uses Awaitility for async assertions

### 5. SAGA Flow Tests (`OrderSagaIntegrationTest`)

End-to-end tests for the complete order SAGA:

- ✅ **Happy Path**: Order → Payment OK → Restaurant Approved → APPROVED
- ✅ **Payment Failure**: Order → Payment Failed → CANCELLED
- ✅ **Restaurant Rejection**: Order → Payment OK → Restaurant Rejected → Compensation → CANCELLED
- ✅ **User Cancellation**: Order → User Cancel → CANCELLING
- ✅ **Concurrent Orders**: Multiple orders created simultaneously

**Key Features:**
- Tests complete business workflows
- Validates SAGA compensation logic
- Tests eventual consistency
- Validates all status transitions

## Prerequisites

Before running the tests, ensure you have:

1. **Java 17** or higher
2. **Maven 3.8+**

**Note**: No Docker required! Tests use H2 in-memory database for easy setup and fast execution.

## Running the Tests

### Run All Tests

```bash
cd SWA/orderservice
mvn clean test
```

### Run Specific Test Class

```bash
# Run only REST API tests
mvn test -Dtest=OrderControllerIntegrationTest

# Run only SAGA tests
mvn test -Dtest=OrderSagaIntegrationTest

# Run only Kafka tests
mvn test -Dtest=OrderKafkaIntegrationTest
```

### Run from IDE

All tests can be run directly from your IDE (IntelliJ IDEA, Eclipse, VS Code):

1. Right-click on test class or method
2. Select "Run Test" or "Debug Test"
3. H2 database will automatically start in-memory

### Run with Coverage

```bash
mvn clean test jacoco:report
```

Coverage report will be generated at: `target/site/jacoco/index.html`

## Test Configuration

### Application Properties

Tests use a separate configuration profile: `application-test.properties`

Key configurations:
- Random server port (`server.port=0`)
- H2 in-memory database in PostgreSQL compatibility mode
- Embedded Kafka on port 9092
- JPA DDL: `create-drop` (fresh schema for each test)
- H2 Console enabled at `/h2-console` (for debugging)

### Base Test Class

`BaseIntegrationTest` provides:
- H2 in-memory database configuration
- Embedded Kafka configuration
- MockMvc initialization
- ObjectMapper for JSON handling
- Shared test utilities

All integration tests extend this base class.

## Debugging Tests

### Enable SQL Logging

Tests already have SQL logging enabled. Check console output for:
```
Hibernate: insert into orders (customer_id, ...) values (?, ...)
```

### Enable Kafka Logging

Add to `application-test.properties`:
```properties
logging.level.org.springframework.kafka=DEBUG
logging.level.org.apache.kafka=DEBUG
```

### Access H2 Console (For Debugging)

While tests are running (with breakpoint), you can access H2 console:
1. Navigate to `http://localhost:<random-port>/h2-console`
2. Use connection URL: `jdbc:h2:mem:testdb`
3. Username: `sa`, Password: (empty)

## Common Issues & Solutions

### Issue: H2 compatibility issues with PostgreSQL-specific SQL

**Solution**: H2 runs in PostgreSQL compatibility mode (`MODE=PostgreSQL`). Most PostgreSQL features are supported. If you encounter issues, check H2 documentation for supported features.

### Issue: Port already in use (9092)

**Solution**: Stop local Kafka or change Embedded Kafka port in `BaseIntegrationTest`

### Issue: Tests timeout waiting for events

**Solution**:
- Check Kafka consumer configuration
- Increase timeout in `await()` statements
- Enable Kafka debug logging

### Issue: Database constraint violations

**Solution**:
- Check `@BeforeEach` cleanup methods
- Ensure unique IDs (use `UUID.randomUUID()`)
- Verify foreign key relationships

## Test Execution Times

Approximate execution times (on average hardware):

- **OrderControllerIntegrationTest**: ~3-5 seconds
- **OrderRepositoryIntegrationTest**: ~2-4 seconds
- **OrderOutboxIntegrationTest**: ~2-3 seconds
- **OrderKafkaIntegrationTest**: ~5-8 seconds
- **OrderSagaIntegrationTest**: ~8-12 seconds

**Total Suite**: ~20-30 seconds

**Benefits of H2**:
- No Docker required
- Faster startup (no container overhead)
- Consistent execution times
- Works offline

## Best Practices

1. **Isolation**: Each test cleans up database in `@BeforeEach`
2. **Idempotency**: Tests can run in any order
3. **Realistic Data**: Uses real PostgreSQL and Kafka, not mocks
4. **Async Testing**: Uses Awaitility for event-driven assertions
5. **Clear Names**: Test methods use descriptive names with `@DisplayName`
6. **AAA Pattern**: Tests follow Arrange-Act-Assert structure

## Continuous Integration

These tests are designed to run in CI/CD pipelines:

```yaml
# Example GitHub Actions
- name: Run Integration Tests
  run: mvn clean verify

# No Docker required - H2 runs in-memory
# Faster CI builds with consistent results
```

## Contributing

When adding new integration tests:

1. Extend `BaseIntegrationTest`
2. Clean up test data in `@BeforeEach`
3. Use `@DisplayName` for clear test descriptions
4. Follow AAA (Arrange-Act-Assert) pattern
5. Use Awaitility for async assertions
6. Add comments explaining complex test scenarios

## Related Documentation

- [H2 Database Documentation](https://www.h2database.com/html/main.html)
- [H2 PostgreSQL Compatibility](https://www.h2database.com/html/features.html#compatibility)
- [Spring Boot Testing](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.testing)
- [Awaitility User Guide](https://github.com/awaitility/awaitility/wiki/Usage)
- [SAGA Pattern](https://microservices.io/patterns/data/saga.html)
- [Transactional Outbox](https://microservices.io/patterns/data/transactional-outbox.html)

## Support

For issues or questions about the tests:
1. Check this README
2. Review test comments and JavaDocs
3. Check application logs in `order-service.log`
4. Review main application configuration in `application.properties`
