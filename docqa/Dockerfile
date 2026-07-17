# Use an official Java 17 runtime as a parent image
FROM eclipse-temurin:17-jdk-alpine

# Set the working directory inside the container
WORKDIR /app

# Copy the packaged jar file into the container
# (We will build this jar file in a moment!)
COPY target/*.jar app.jar

# Expose port 8080 to the outside world
EXPOSE 8080

# Run the jar file


#To Run the Docker File it needs a Entry Point and here is how to decalre a entry point 
ENTRYPOINT ["java", "-jar", "app.jar"]
