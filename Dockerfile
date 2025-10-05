# ------------------------------
# Stage 1: Build the application
# ------------------------------
FROM maven:3.9.5-eclipse-temurin-17 AS build

# Set working directory
WORKDIR /app

# Copy only pom.xml first to cache dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy source code
COPY src ./src

# Build the jar, skipping tests
RUN mvn clean package -DskipTests

# ------------------------------
# Stage 2: Run the application
# ------------------------------
FROM eclipse-temurin:17-jdk-alpine

# Create a non-root user for safety
RUN addgroup -S spring && adduser -S spring -G spring
USER spring

# Set working directory
WORKDIR /app

# Copy the jar from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose default Spring Boot port
EXPOSE 8080

# Optional JVM tuning
ENV JAVA_OPTS=""

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
