# Stage 1: Build the application
FROM maven:3.9.9-openjdk-21 AS build
WORKDIR /transaction

# Copy the pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy the source code and build the application
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Run the application
FROM openjdk:21-jre-slim
WORKDIR /transaction

# Copy the jar file from the build stage
COPY --from=build /transaction/target/transaction-*.jar app.jar

# Expose the port the application runs on
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]


