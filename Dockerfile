# Stage 1: Build Java Spring Boot
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app

# Copy maven wrapper & pom
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw

# Download dependencies
RUN ./mvnw dependency:go-offline -B

# Copy source & build
COPY src/ src/
RUN ./mvnw clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Non-root user for security
RUN addgroup -S pennywise && adduser -S pennywise -G pennywise
USER pennywise

COPY --from=builder /app/target/pennywise-backend-*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]
