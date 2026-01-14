FROM eclipse-temurin:25 AS build
COPY . /app
WORKDIR /app

RUN /app/mvnw clean package -DskipTests

FROM eclipse-temurin:25

COPY --from=build /app/target /app/target

RUN groupadd --system backend && useradd --create-home --system --gid backend backend
RUN chown -R backend:backend /app
USER backend

WORKDIR /app

ENTRYPOINT ["java","-jar","target/backend-0.0.1-SNAPSHOT.jar"]
