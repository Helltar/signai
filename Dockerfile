FROM gradle:8.5-jdk21-alpine AS builder

WORKDIR /app

COPY build.gradle.kts gradle.properties settings.gradle.kts ./
RUN gradle shadowJar -x test --no-daemon
COPY src ./src
RUN gradle shadowJar --no-daemon

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY --from=builder /app/build/libs/*.jar signai.jar

RUN adduser -u 10001 -D -s /bin/sh signai
RUN chown -R signai:signai /app
USER signai

ENTRYPOINT ["java", "-jar", "signai.jar"]
