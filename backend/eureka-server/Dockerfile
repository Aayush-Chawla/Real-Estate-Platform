FROM eclipse-temurin:21-jdk-jammy AS runtime

# Set the working directory
WORKDIR /app

# Copy the built JAR from target/
COPY target/eureka-server-0.0.1-SNAPSHOT.jar app.jar

# Expose the Eureka Server port
EXPOSE 8761

# Run the Eureka Server
ENTRYPOINT ["java", "-jar", "app.jar"]
