# ========================
# Step 1: Build stage
# ========================
FROM maven:3.8.5-openjdk-17 AS builder

# Set working directory inside the container
WORKDIR /app

# Copy pom.xml and download dependencies (cache optimization)
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy source code
COPY src ./src

# Build the application (skip tests for faster build)
RUN mvn clean package -DskipTests

# ========================
# Step 2: Run stage
# ========================
FROM openjdk:17-jdk-slim

# Set working directory inside the container
WORKDIR /app

# Copy the JAR file from builder stage
# Rename it explicitly to app.jar to avoid "no main manifest attribute" errors
COPY --from=builder /app/target/*SNAPSHOT.jar app.jar

# Expose Spring Boot default port
EXPOSE 8080

# Run the JAR
ENTRYPOINT ["java", "-jar", "app.jar"]