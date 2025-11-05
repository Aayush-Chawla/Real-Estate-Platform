# Use the same JDK base as eureka-server for consistency
FROM eclipse-temurin:21-jdk-jammy AS runtime

# Set working directory
WORKDIR /app

# Copy the built JAR into the container
COPY target/auth-service-0.0.1-SNAPSHOT.jar app.jar

# Expose the service port
EXPOSE 8081

# Set environment variables (override via compose)
ENV SPRING_PROFILES_ACTIVE=prod

# Run the service
ENTRYPOINT ["java", "-jar", "app.jar"]
