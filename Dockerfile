FROM eclipse-temurin:21-jdk-alpine

ARG JAR_FILE=build/libs/open-football-api*SNAPSHOT.jar
COPY ${JAR_FILE} app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app.jar"]
