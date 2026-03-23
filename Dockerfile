# Use lightweight Java image
FROM openjdk:17-jdk-slim

# Set working directory
WORKDIR /app

# Copy jar file into container
COPY target/*.jar app.jar

# Expose port (Spring Boot default)
EXPOSE 9091

# Run application
ENTRYPOINT ["java", "-jar", "app.jar"]