FROM maven:3.8.5-amazoncorretto-8 as build
WORKDIR /build
ADD . /build/
RUN mvn package

FROM openjdk:8-jre-slim
WORKDIR /app
COPY --from=build /build/target/docker-image-deleter-full.jar /app/docker-image-deleter.jar
ENTRYPOINT [ "java", "-jar", "/app/docker-image-deleter.jar" ]
# ENTRYPOINT [ "java", "-jar", "/build/target/docker-image-deleter-full.jar" ]
