
# BUILD (Compilación)

FROM amazoncorretto:17.0.17-alpine3.22 AS builder

WORKDIR /app

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src

RUN ./mvnw clean package -DskipTests

# RUN (Ejecución)

FROM amazoncorretto:17.0.17-alpine3.22

WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080


CMD ["java", "-jar", "app.jar"]
