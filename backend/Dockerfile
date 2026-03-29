FROM eclipse-temurin:21-jdk AS build

WORKDIR /workspace

COPY gradlew gradlew
COPY gradlew.bat gradlew.bat
COPY gradle gradle
COPY settings.gradle settings.gradle
COPY build.gradle build.gradle
COPY src src

RUN sed -i 's/\r$//' gradlew && chmod +x gradlew
RUN ./gradlew bootJar --no-daemon

FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build /workspace/build/libs/*.jar app.jar

EXPOSE 8085

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
