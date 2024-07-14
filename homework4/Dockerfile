FROM maven:3.8.5-openjdk-17-slim AS build

WORKDIR /app

# Copy pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy the rest of the project files
COPY src ./src

# Package the application
RUN mvn clean package

FROM openjdk:17-jdk-alpine

# Set working directory
WORKDIR /app

# Copy the entire Apache Tomcat directory to the container
COPY apache-tomcat-10.1.25 /app/apache-tomcat-10.1.25

# Copy the compiled application to the Tomcat webapps directory
COPY --from=build /app/target/homework3 /app/apache-tomcat-10.1.25/webapps/homework3

# Expose the necessary port for Tomcat
EXPOSE 8080

# Set the entrypoint to start Tomcat
ENTRYPOINT ["/app/apache-tomcat-10.1.25/bin/catalina.sh", "run"]
