# 1️⃣ Byggesteg: Bruk Maven for å bygge koden
FROM maven:3.9.6-eclipse-temurin-21 AS build

WORKDIR /usr/src/app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY . .
RUN mvn clean package -DskipTests

# 2️⃣ Runtime-steg: Bruk et mindre OpenJDK 21-image for kjøring
FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app
COPY --from=build /usr/src/app/target/*.jar app.jar

CMD ["java", "-jar", "app.jar"]
