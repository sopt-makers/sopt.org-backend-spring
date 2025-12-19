FROM eclipse-temurin:17-jdk-jammy AS build
WORKDIR /app
COPY . /app
RUN apt-get update && apt-get install -y curl unzip
RUN ./gradlew bootJar -x test

FROM eclipse-temurin:17-jdk-jammy AS production
WORKDIR /app

COPY --from=build /app/build/libs/*.jar app.jar

ENTRYPOINT ["java","-jar","/app/app.jar"]
