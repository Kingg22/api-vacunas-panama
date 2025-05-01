# Test Refactoring Guide: From Spring to Framework-Independent Tests

This guide explains how to refactor tests from Spring-dependent to framework-independent tests using Rest-Assured.

## Overview

The goal of this refactoring is to make tests independent of the Spring framework, using Rest-Assured with Kotlin
extensions for a better DSL. This approach provides several benefits:

1. Tests are decoupled from the framework, making them more maintainable
2. Rest-Assured provides a more expressive and readable DSL for API testing
3. Tests can be run without loading the entire Spring context, making them faster

## Key Components

### TestBase Class

The `TestBase` class provides a foundation for all framework-independent tests:

- Sets up test containers for Redis and PostgreSQL
- Configures Rest-Assured with default settings
- Provides helper methods for making HTTP requests

All test classes should extend this class instead of using Spring annotations.

### Utility Functions

- `retrieveFileJsonWithoutSpring`: A replacement for the Spring-dependent `retrieveFileJson` function
- `removeMetadata`: Removes non-testable information from response bodies
- `extractJsonToken`: Extracts specific values from JSON responses

## Refactoring Pattern

Follow these steps to refactor a test class:

1. Change the class to extend `TestBase` instead of using Spring annotations:

```kotlin
// Before
@ActiveProfiles("test")
@Import(TestcontainersConfiguration::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MyControllerTest @Autowired constructor(private val webTestClient: WebTestClient) {
    // Test methods
}

// After
class MyControllerTest : TestBase() {
    // Test methods
}
```

2. Replace WebTestClient calls with Rest-Assured:

```kotlin
// Before
webTestClient.get()
    .uri("/my-endpoint")
    .exchange()
    .expectStatus().isOk
    .expectBody()
    .consumeWith {
        val responseBody = it.responseBody?.toString(Charsets.UTF_8)
        assertNotNull(responseBody)
        responseBody.removeMetadata() shouldContain "\"expected_value\""
    }

// After
val responseBody = Given {
    spec(requestSpec)
} When {
    get("/my-endpoint")
} Then {
    statusCode(HttpStatus.SC_OK)
} Extract {
    body().asString()
}

assertNotNull(responseBody)
responseBody.removeMetadata() shouldContain "\"expected_value\""
```

3. Replace Spring's authentication with Rest-Assured headers:

```kotlin
// Before
webTestClient
    .mutateWith(
        mockJwt().jwt {
            it.subject("user-id")
            it.claim("role", "ADMIN")
        }
    )
    .get()
    .uri("/secured-endpoint")

// After
val jwt = "" // obteined with previous login

Given {
    spec(requestSpec)
    header("Authorization", "Bearer $jwt")
} When {
    get("/secured-endpoint")
}
```

## Examples

See the following refactored test classes for examples:

- `BulkControllerTest`: Basic GET/POST requests
- `VacunaControllerTest`: Using retrieveFileJson
- `TokenControllerTest`: JWT authentication
- `PacienteControllerTest`: Pre authorized request
- `DireccionControllerTest`: JSON comparison with shouldEqualJson

## Running Tests

Tests can be run using Gradle:

```bash
./gradlew test
```

Or run individual test classes from your IDE.
