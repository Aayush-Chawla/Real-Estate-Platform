FROM eclipse-temurin:21-jdk-jammy AS runtime

# Set work directory
WORKDIR /app

# Copy the built JAR file from your target folder
COPY target/api-gateway-0.0.1-SNAPSHOT.jar app.jar

# Expose the port used by the app
EXPOSE 9090

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
