# Étape de build
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

# Copier les fichiers Gradle
COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle ./

# Rendre gradlew exécutable
RUN chmod +x gradlew

# Télécharger les dépendances
RUN ./gradlew dependencies --no-daemon

# Copier le code source et les fichiers OpenAPI (si présent)
COPY src src
COPY openapi openapi

# Construire le JAR
RUN ./gradlew bootJar --no-daemon

# Étape finale
FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]