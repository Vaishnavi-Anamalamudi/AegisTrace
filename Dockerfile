FROM eclipse-temurin:17-jdk AS build
WORKDIR /workspace
COPY .mvn .mvn
COPY mvnw pom.xml ./
RUN ./mvnw -q -DskipTests dependency:go-offline
COPY src src
RUN ./mvnw -q -DskipTests package

FROM eclipse-temurin:17-jre
RUN apt-get update \
    && apt-get install -y --no-install-recommends nmap \
    && rm -rf /var/lib/apt/lists/* \
    && groupadd --system aegistrace \
    && useradd --system --gid aegistrace --home-dir /app --shell /usr/sbin/nologin aegistrace
WORKDIR /app
COPY --from=build /workspace/target/*.jar app.jar
RUN chown -R aegistrace:aegistrace /app
USER aegistrace
EXPOSE 8084
ENTRYPOINT ["java", "-jar", "/app/app.jar", "--spring.profiles.active=docker"]
