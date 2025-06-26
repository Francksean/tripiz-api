# Étape 1 : Image légère JRE Alpine basée sur Temurin (Java 21)
FROM eclipse-temurin:21-jre-alpine

# Créer un utilisateur non-root pour plus de sécurité
RUN addgroup -S spring && adduser -S spring -G spring

# Définir le répertoire de travail
WORKDIR /app

# Argument pour spécifier le nom du fichier JAR à copier
ARG JAR_FILE=build/libs/api-0.0.1-SNAPSHOT.jar

# Copier le JAR dans le conteneur
COPY ${JAR_FILE} app.jar

# Donner les permissions au user spring (optionnel mais recommandé)
RUN chown -R spring:spring /app

# Passer à l'utilisateur spring
USER spring:spring

# Exposer le port
EXPOSE 8080

# Définir les options Java via ENV pour éviter les soucis d'expansion de variables dans ENTRYPOINT
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75"

# Entrée du conteneur
ENTRYPOINT exec java $JAVA_OPTS -jar app.jar
