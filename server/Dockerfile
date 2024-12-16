# Base image for building
FROM bellsoft/liberica-openjdk-alpine:21.0.4 AS build

# Set the working directory
WORKDIR /app

# Copy the Gradle wrapper and settings files
COPY gradlew settings.gradle build.gradle ./
COPY gradle ./gradle

# Copy the source code
COPY src ./src

# Grant execution permissions for the Gradle wrapper
RUN chmod +x ./gradlew

# Run the build
RUN ./gradlew build --exclude-task test

# Base image for running
FROM bellsoft/liberica-openjdk-alpine:21.0.4

# Copy the built JAR from the build stage
COPY --from=build /app/build/libs/*.jar /app/server.jar

# Author label
LABEL authors="Traffic-Hunter"

# Expose the port the app runs on
EXPOSE 9100

# Command to run the application
ENTRYPOINT ["java", "-jar", "/app/server.jar"]