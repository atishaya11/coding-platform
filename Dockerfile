FROM openjdk:8-jdk-alpine

COPY target/coding-platform-0.0.1-SNAPSHOT.jar coding-platform-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","/coding-platform-0.0.1-SNAPSHOT.jar","--server.port=80"]