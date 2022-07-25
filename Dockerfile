FROM eclipse-temurin:11-jre
COPY ./target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar","/app.jar"]