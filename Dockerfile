# Étape 1 : Build de l'application
FROM eclipse-temurin:21-jdk-alpine AS build

# Installer les outils nécessaires (curl, bash, etc.)
RUN apk add --no-cache bash curl unzip

# Crée l'utilisateur spring
RUN addgroup -S spring && adduser -S spring -G spring

WORKDIR /app

# Copier les fichiers du projet
COPY . .

# Donner les droits d'exécution à Gradle wrapper s'il est présent
RUN chmod +x ./gradlew

# Build de l'application sans les tests
RUN ./gradlew clean build -x test -x check --no-daemon

# Étape 2 : Image finale allégée
FROM eclipse-temurin:21-jre-alpine

# Créer l'utilisateur spring
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

WORKDIR /app

# Copier le jar compilé depuis le build stage
COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080

ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75"
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
