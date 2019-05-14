FROM openjdk:8-jdk-alpine
RUN apk --no-cache add curl
COPY ./target/*.jar app.jar
ENTRYPOINT ["java", "-jar","/app.jar"]