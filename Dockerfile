FROM maven:3.8.5-eclipse-temurin-18 as build
WORKDIR /build
COPY * /build/
RUN mvn package

FROM eclipse-temurin:18-jre
WORKDIR /app
COPY --from=build /build/target/docker-image-deleter-full.jar /app/docker-image-deleter.jar
ENTRYPOINT [ "java", "-jar", "/app/docker-image-deleter.jar" ]
