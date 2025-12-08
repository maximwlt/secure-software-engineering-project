FROM eclipse-temurin:21
COPY . /app
WORKDIR /app

RUN /app/mvnw clean package -DskipTests

ENTRYPOINT ["java","-jar","target/backend-0.0.1-SNAPSHOT.jar"]