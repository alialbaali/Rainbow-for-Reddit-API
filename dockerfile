FROM gradle:8-jdk11 AS build
COPY --chown=gradle:gradle . /app/gradle
WORKDIR /app/gradle
RUN gradle buildFatJar --no-daemon

FROM openjdk:11
EXPOSE 8080:8080
COPY --from=build /app/gradle/build/libs/*.jar /app/rainbow.jar
ENTRYPOINT ["java","-jar","/app/rainbow.jar"]