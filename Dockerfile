# Dockerfile
FROM openjdk:21-jdk-slim
WORKDIR /app
COPY target/leave-request-system.jar app.jar
#EXPOSE 8787
ENTRYPOINT ["java", "-jar", "app.jar"]