# Movie Catalog Service

A Spring Boot microservice that provides personalized movie catalogs by aggregating movie information and user ratings from downstream services.

## 📋 Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Architecture](#architecture)
- [Prerequisites](#prerequisites)
- [Project Structure](#project-structure)
- [Getting Started](#getting-started)
- [API Documentation](#api-documentation)
- [Configuration](#configuration)
- [Running Multiple Instances](#running-multiple-instances)
- [Testing](#testing)
- [Troubleshooting](#troubleshooting)
- [Future Enhancements](#future-enhancements)
- [Contributing](#contributing)
- [License](#license)

## Overview

This microservice is part of a distributed system that demonstrates inter-service communication patterns in a Spring Cloud ecosystem. It aggregates data from `movie-info-service` and `ratings-data-service` to provide a unified movie catalog for users.

**Technology Stack:**
- Java 21
- Spring Boot 3.x
- Spring Cloud (with Eureka discovery)
- Maven 3.6+
- RestTemplate with `@LoadBalanced` for service-to-service communication

## Features

- RESTful API for retrieving user-specific movie catalogs
- Load-balanced service discovery via Eureka
- Aggregates data from multiple microservices
- Supports multiple concurrent instances
- Simple domain-driven design

## Architecture

### Service Dependencies

```
┌─────────────────────────┐
│  Movie Catalog Service  │
│     (Port: 8081)        │
└───────────┬─────────────┘
            │
            ├──────────────────────┐
            │                      │
            ▼                      ▼
┌─────────────────────┐  ┌──────────────────────┐
│ Ratings Data Service│  │ Movie Info Service   │
│ /ratingsdata/users  │  │ /movies/{movieId}    │
└─────────────────────┘  └──────────────────────┘
```

### Data Flow

1. Client requests catalog for a user: `GET /catalog/{userId}`
2. Service fetches user ratings from `ratings-data-service`
3. For each rating, service fetches movie details from `movie-info-service`
4. Service aggregates data and returns enriched catalog items

## Prerequisites

- **Java 21 (JDK)** - [Download here](https://adoptium.net/)
- **Maven 3.6+** - Or use the included Maven wrapper (`mvnw`)
- **Eureka Server** (optional) - For service discovery; otherwise, services must be reachable via configured hostnames

## Project Structure

```
movie-catalog-service/
├── HELP.md
├── README.md
├── pom.xml
├── mvnw
├── mvnw.cmd
├── run_and_stop.sh                    # Helper script for running multiple instances
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── io/kipruto/movie_catalog_service/
│   │   │       ├── MovieCatalogServiceApplication.java    # Main application
│   │   │       ├── models/
│   │   │       │   ├── CatalogItem.java        # Aggregated response model
│   │   │       │   ├── Movie.java              # Movie entity
│   │   │       │   ├── Rating.java             # Rating entity
│   │   │       │   └── UserRating.java         # User ratings wrapper
│   │   │       └── resources/
│   │   │           └── MovieCatalogResource.java    # REST controller
│   │   └── resources/
│   │       ├── application.properties          # Application configuration
│   │       ├── static/                         # Static resources
│   │       └── templates/                      # Template files
│   └── test/
│       └── java/
│           └── io/kipruto/movie_catalog_service/
│               └── MovieCatalogServiceApplicationTests.java
└── target/                                     # Build output (generated)
    ├── classes/
    ├── movie-catalog-service-0.0.1-SNAPSHOT.jar
    └── ...
```

### Key Components

- **MovieCatalogResource.java** - REST controller exposing the `/catalog/{userId}` endpoint
- **Models Package** - Domain objects for Movie, Rating, UserRating, and CatalogItem
- **application.properties** - Service configuration including name, port, and Eureka settings

## Getting Started

### Building the Application

```bash
# Using Maven wrapper (recommended)
./mvnw clean package

# Or using system Maven
mvn clean package
```

This produces `target/movie-catalog-service-0.0.1-SNAPSHOT.jar`

### Running the Application

**Default configuration (port 8081):**

```bash
java -jar target/movie-catalog-service-0.0.1-SNAPSHOT.jar
```

**Custom port:**

```bash
java -jar target/movie-catalog-service-0.0.1-SNAPSHOT.jar --server.port=8084
```

**With Spring profiles:**

```bash
java -jar target/movie-catalog-service-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev
```

## API Documentation

### Get User Catalog

Retrieves the movie catalog for a specific user, including ratings and movie details.

**Endpoint:** `GET /catalog/{userId}`

**Path Parameters:**
- `userId` (string) - The unique identifier for the user

**Request Example:**

```bash
curl -s http://localhost:8081/catalog/user123
```

**Response Example:**

```json
[
  {
    "name": "The Shawshank Redemption",
    "desc": "Two imprisoned men bond over a number of years",
    "rating": 5
  },
  {
    "name": "The Godfather",
    "desc": "The aging patriarch of an organized crime dynasty",
    "rating": 4
  }
]
```

**Response Status Codes:**
- `200 OK` - Successfully retrieved catalog
- `500 Internal Server Error` - Service dependency unavailable

### Data Models

#### CatalogItem (Response)
```json
{
  "name": "string",
  "desc": "string",
  "rating": "integer"
}
```

#### Movie (from movie-info-service)
```json
{
  "movieId": "string",
  "name": "string"
}
```

#### Rating (from ratings-data-service)
```json
{
  "movieId": "string",
  "rating": "integer"
}
```

#### UserRating (from ratings-data-service)
```json
{
  "userRating": [
    {
      "movieId": "string",
      "rating": "integer"
    }
  ]
}
```

## Configuration

Key configuration properties in `src/main/resources/application.properties`:

```properties
# Application identity
spring.application.name=movie-catalog-service

# Server configuration
server.port=8081

# Eureka client configuration (if using service discovery)
eureka.client.service-url.defaultZone=http://localhost:8761/eureka
eureka.instance.instance-id=${spring.application.name}:${random.value}
```

### Service Dependencies Configuration

This service expects the following services to be available:

| Service | Endpoint | Returns |
|---------|----------|---------|
| ratings-data-service | `/ratingsdata/users/{userId}` | UserRating JSON |
| movie-info-service | `/movies/{movieId}` | Movie JSON |

**With Eureka:** Services are discovered automatically by name

**Without Eureka:** Configure explicit URLs or map service names in `/etc/hosts`

## Running Multiple Instances

A helper script is provided to test load balancing with multiple instances:

```bash
# Make script executable
chmod +x run_and_stop.sh

# Run three instances on ports 8088, 8084, and 8085
./run_and_stop.sh
```

The script automatically:
- Starts three instances with unique instance IDs
- Runs them for 20 minutes
- Stops all instances automatically

**Manual multiple instances:**

```bash
# Terminal 1
java -jar target/movie-catalog-service-0.0.1-SNAPSHOT.jar --server.port=8081

# Terminal 2
java -jar target/movie-catalog-service-0.0.1-SNAPSHOT.jar --server.port=8082

# Terminal 3
java -jar target/movie-catalog-service-0.0.1-SNAPSHOT.jar --server.port=8083
```

## Testing

### Running Tests

```bash
# Run all tests
./mvnw test

# Run tests with coverage
./mvnw test jacoco:report

# Run specific test class
./mvnw test -Dtest=MovieCatalogServiceApplicationTests
```

### Integration Testing

For full integration testing, ensure all dependent services are running:

1. Start Eureka server
2. Start `ratings-data-service`
3. Start `movie-info-service`
4. Start this service
5. Test the catalog endpoint

## Troubleshooting

### Service-to-Service Communication Failures

**Symptoms:** `RestTemplate` errors, connection refused, or unknown host exceptions

**Solutions:**
- Verify dependent services are running and healthy
- Check Eureka dashboard to confirm service registration
- Verify network connectivity between services
- Check service names match exactly in configuration

### Port Conflicts

**Symptoms:** "Port already in use" error on startup

**Solutions:**
```bash
# Find process using port
lsof -i :8081

# Kill process
kill -9 <PID>

# Or use different port
java -jar target/*.jar --server.port=80**
```

### Eureka Registration Issues

**Symptoms:** Service not appearing in Eureka dashboard

**Solutions:**
- Verify `eureka.client.service-url.defaultZone` points to correct Eureka server
- Check Eureka server is running and accessible
- Review application logs for registration errors
- Ensure `spring.application.name` is set correctly

### Maven Build Failures

```bash
# Clean and rebuild
./mvnw clean install

# Verbose diagnostics
./mvnw -X package

# Skip tests if needed
./mvnw clean package -DskipTests
```

## Future Enhancements

- [ ] **Resilience Patterns** - Add Circuit Breaker using Resilience4j
- [ ] **Reactive Programming** - Replace RestTemplate with WebClient for non-blocking calls
- [ ] **API Documentation** - Integrate Swagger/OpenAPI
- [ ] **Input Validation** - Add Bean Validation annotations
- [ ] **Error Handling** - Implement global exception handler with detailed error responses
- [ ] **Caching** - Add Redis caching for frequently accessed catalogs
- [ ] **Monitoring** - Integrate Spring Boot Actuator and Micrometer
- [ ] **Security** - Add OAuth2/JWT authentication
- [ ] **Containerization** - Create Dockerfile and docker-compose setup

## Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/amazing-feature`
3. Commit your changes: `git commit -m 'Add amazing feature'`
4. Push to the branch: `git push origin feature/amazing-feature`
5. Open a Pull Request

**Guidelines:**
- Write clear commit messages
- Add tests for new features
- Update documentation as needed
- Follow existing code style

## License

This project currently MIT license. 


## Contact & Support

- **Issues:** Open an issue on the repository
- **Questions:** Contact the project maintainer
- **Documentation:** See `HELP.md` for Spring Boot reference documentation

---

**Related Projects:**
- [ratings-data-service](https://github.com/KiprutoKelvin/Kilele-MicroServices/tree/main/ratings-data-service) - Provides user ratings data
- [movie-info-service](https://github.com/KiprutoKelvin/Kilele-MicroServices/tree/main/movie-info-service) - Provides movie metadata
- [discovery-server](https://github.com/KiprutoKelvin/Kilele-MicroServices/tree/main/discovery-server) - Service discovery server