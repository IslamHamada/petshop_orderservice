FROM eclipse-temurin:17-jdk

ARG JAR_FILE=target/*.jar

COPY ${JAR_FILE} order-service.jar

ENTRYPOINT ["java", "-jar", "order-service.jar"]

EXPOSE 8084