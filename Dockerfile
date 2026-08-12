FROM gradle:9.0.0-jdk21-alpine AS builder

WORKDIR /app

COPY build.gradle.kts gradle.properties settings.gradle.kts ./
COPY gradle/libs.versions.toml gradle/

RUN gradle --no-daemon shadowJar

COPY src ./src
RUN gradle --no-daemon shadowJar


FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

RUN adduser -u 10001 -D -s /bin/sh signai

USER signai

COPY --from=builder /app/build/libs/*-all.jar signai.jar

ENTRYPOINT ["java", "-jar", "signai.jar"]
