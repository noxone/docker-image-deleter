FROM maven:3.8.5-eclipse-temurin-18
WORKDIR /build
COPY * /build/
RUN 

FROM eclipse-temurin:18-jre
WORKDIR /app
COPY ./target/docker-image-deleter-1.0-SNAPSHOT-jar-with-dependencies.jar /app/docker-image-deleter.jar
ENTRYPOINT [ "java", "-jar", "/app/docker-image-deleter.jar" ]
