# Transaction Manager

## Description
A simple application to manage financial transactions using Java 21 and Spring Boot.

## Requirements
- Java 21
- Maven 3.9.9

## Build and Run
```bash
mvn clean install
java -jar target/transaction-1.0-SNAPSHOT.jar
```

## Docker
```bash
docker build -t transaction-app .
docker run -p 8080:8080 transaction-app
```

## Kubernetes
```bash
kubectl apply -f k8s/deployment.yaml
kubectl apply -f k8s/service.yaml
```

## API Endpoints
- `POST /api/transactions` - Create a new transaction
- `DELETE /api/transactions/{id}` - Delete a transaction
- `PUT /api/transactions/{id}` - Modify a transaction
- `GET /api/transactions?page=0&size=10` - List all transactions with pagination

## External Libraries
- spring-boot-starter-web: For building the RESTful API
- spring-boot-starter-validation: For validate the parameters
- spring-boot-starter-cache: Provide auto-configuration support for declarative caching
- caffeine: High-performance Java caching library to realize data cache
- lombok: reduces boilerplate code
- spring-boot-starter-test: For test
- JUnit: For unit testing

## Caching
This application uses Spring's cache and caffeine to cache transactions.

## Validation and Exception Handling
- Parameters are validated using annotations support by jakarta.validation.
- Global exception handling is implemented to handle validation errors and unexpected exceptions.

## Testing
- Unit tests are provided to ensure the robustness and reliability of the API.
- Stress tests evaluates a system's stability, performance, and scalability under extreme high-load conditions  

> Plz check the result of Stress Tests in surefier-report.html