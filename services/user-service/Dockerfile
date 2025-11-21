# =========================
# 1. Build Stage
# =========================
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

# Copy pom.xml first for dependency caching
COPY pom.xml .

# Download dependencies (using mvn, not ./mvnw)
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the JAR (using mvn, not ./mvnw)
RUN mvn clean package -DskipTests


# =========================
# 2. Runtime Stage
# =========================
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

# Install curl for healthcheck
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Create folders
RUN mkdir -p /app/uploads /app/logs

# Copy JAR from build stage
COPY --from=build /app/target/*.jar app.jar

# Create non-root user
RUN useradd -r -u 1001 appuser && chown -R appuser:appuser /app
USER appuser

# Expose ports
EXPOSE 8081
EXPOSE 5005

# Healthcheck
HEALTHCHECK --interval=30s --timeout=5s --start-period=40s --retries=3 \
  CMD curl -f http://localhost:8081/actuator/health || exit 1

# Run with JAVA_OPTS from environment
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]