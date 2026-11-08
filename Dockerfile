# ===== Étape 1 : build (JDK) =====
FROM eclipse-temurin:25-jdk AS build
WORKDIR /app

# Dépendances d'abord : cette couche reste en cache tant que pom.xml ne change pas
COPY mvnw pom.xml ./
COPY .mvn .mvn
# Supprime les fins de ligne Windows (CRLF) qui casseraient le script sous Linux
RUN sed -i 's/\r$//' mvnw && chmod +x mvnw
RUN ./mvnw -B dependency:go-offline

COPY src src
# Les tests tournent dans la CI ; contextLoads échouerait ici faute de base
RUN ./mvnw -B package -DskipTests

# ===== Étape 2 : run (JRE seul, image plus légère) =====
FROM eclipse-temurin:25-jre
WORKDIR /app

# Ne jamais exécuter l'application en root
RUN groupadd --system spring && useradd --system --gid spring spring
USER spring

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
# MaxRAMPercentage : taille du tas calculée sur la mémoire du conteneur
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]
