FROM openjdk:17-jdk-slim AS build
WORKDIR /app
COPY . /app
RUN ./gradlew build

FROM openjdk:17-jdk-slim AS production
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar

ENTRYPOINT ["java","-jar","/app/app.jar"]