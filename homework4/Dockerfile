# Stage 1: Build Stage
FROM maven:3.8.5-openjdk-17-slim AS build

WORKDIR /app

# Copy the Maven project descriptor
COPY pom.xml .

# Copy the source code
COPY src ./src

# Build the application
RUN mvn clean package

FROM liquibase/liquibase:latest AS liquibase

WORKDIR /liquibase

# Copy Liquibase changelog file
COPY src/resources/db/changelog /liquibase/changelog

# Set Liquibase environment variables
ENV LIQUIBASE_DRIVER=org.postgresql.Driver

# Default command to run Liquibase update
CMD ["update", "--changeLogFile=/changelog/changelog.xml", "--url=jdbc:postgresql://db:5432/coworking", "--username=postgres", "--password=root"]

FROM openjdk:17-jdk-slim AS runtime

WORKDIR /app

# Copy Apache Tomcat and the built application from the build stage
COPY apache-tomcat-9.0.91 /app/apache-tomcat-9.0.91
COPY --from=build /app/target/homework4-1.0-SNAPSHOT /app/apache-tomcat-9.0.91/webapps/homework4

# Expose port 8080
EXPOSE 8080

# Set the entrypoint to start Tomcat
ENTRYPOINT ["/app/apache-tomcat-9.0.91/bin/catalina.sh", "run"]