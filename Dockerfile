# Build stage - compile and package with Maven
FROM maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /build

# Copy pom.xml first for dependency caching
COPY pom.xml .

# Download dependencies
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build JAR (skip tests)
RUN mvn clean package -DskipTests -B

# List target directory (optional)
RUN ls -la /build/target/

# Runtime stage - smaller image
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Install CA certificates for HTTPS support
RUN apk add --no-cache ca-certificates

# Copy JAR from builder stage
COPY --from=builder /build/target/*.jar app.jar

# Expose port
EXPOSE 8080

# JVM options
ENV JAVA_OPTS="-Xms512m -Xmx1024m"

# Run Spring Boot app
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/app.jar"]