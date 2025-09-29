# ========================
# Step 1: Build stage
# ========================
FROM maven:3.8.5-eclipse-temurin-17 AS builder

WORKDIR /app

# Copy pom.xml first to leverage Docker cache
COPY pom.xml .
RUN mvn -B -q dependency:go-offline

# Copy project source
COPY src ./src

# Build application (skip tests for faster build)
RUN mvn -B clean package -DskipTests

# ========================
# Step 2: Run stage
# ========================
FROM eclipse-temurin:17-jre

WORKDIR /app

# Copy jar (handles both SNAPSHOT and release jars)
COPY --from=builder /app/target/*.jar app.jar

# Document port (Render overrides with $PORT)
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]