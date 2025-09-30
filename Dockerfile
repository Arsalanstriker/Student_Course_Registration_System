# Use official Java 17 runtime
FROM openjdk:17-jdk-slim

# Set working directory
WORKDIR /app

# Copy JAR from Maven build
COPY target/student-course-registration-1.0-SNAPSHOT.jar app.jar

# Expose port (optional, if later turned into API)
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
