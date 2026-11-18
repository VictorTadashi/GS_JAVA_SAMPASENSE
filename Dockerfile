# ===== Etapa 1: build com Maven =====
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -B -q dependency:go-offline
COPY src ./src
RUN mvn -B -q clean package -DskipTests

# ===== Etapa 2: imagem final só com o JRE =====
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/sampasense-api.jar app.jar

# Memória ajustada para o plano gratuito do Render (512 MB)
ENV JAVA_OPTS="-Xmx300m -XX:+UseSerialGC"
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
