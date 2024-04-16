FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY hh-bot/build/libs/*.jar ./app.jar
ENTRYPOINT ["java","-jar","app.jar"]