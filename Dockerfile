FROM openjdk:17-jdk-slim AS build
WORKDIR /app
COPY . /app
RUN apt-get update && apt-get install -y curl unzip
RUN ./gradlew bootJar -x test

FROM openjdk:17-jdk-slim AS production
RUN apt-get update && apt-get install -y \
   chromium \
   chromium-driver \
   xvfb \
   && rm -rf /var/lib/apt/lists/*

WORKDIR /app

# Set Chrome environment variables
ENV CHROME_BIN=/usr/bin/chromium
ENV CHROMEDRIVER_BIN=/usr/bin/chromedriver
ENV IS_DOCKER_BUILD=true

COPY --from=build /app/build/libs/*.jar app.jar

ENTRYPOINT ["java","-jar","/app/app.jar"]