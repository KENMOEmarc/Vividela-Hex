FROM maven:3.9.9-eclipse-temurin-21-alpine AS build

WORKDIR /workspace

COPY pom.xml lombok.config ./
COPY src ./src

RUN mvn -B -Dmaven.test.skip=true package

FROM eclipse-temurin:21-jre-alpine AS runtime

RUN addgroup -S spring && adduser -S spring -G spring
WORKDIR /app

COPY --from=build --chown=spring:spring /workspace/target/vividela-backend-1.0.0.jar app.jar

USER spring:spring
EXPOSE 8080

ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-jar", "/app/app.jar"]
