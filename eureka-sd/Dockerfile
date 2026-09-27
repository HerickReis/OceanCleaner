# ===== Etapa 1: build =====
# Imagem com Maven + JDK 21 apenas para compilar o projeto
FROM maven:3.9-eclipse-temurin-21-alpine AS build
WORKDIR /build

# Copia só o pom.xml primeiro para aproveitar o cache de camadas:
# as dependências só são baixadas de novo quando o pom.xml mudar
COPY pom.xml .
RUN mvn -B dependency:go-offline

# Copia o código-fonte e gera o .jar (os testes rodam no pipeline de CI)
COPY src ./src
RUN mvn -B package -DskipTests

# ===== Etapa 2: runtime =====
# Imagem final enxuta: apenas o JRE, sem Maven nem código-fonte
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Executa a aplicação com um usuário sem privilégios de root
RUN addgroup -S spring && adduser -S spring -G spring
USER spring

COPY --from=build /build/target/*.jar app.jar

EXPOSE 5050
ENTRYPOINT ["java", "-jar", "app.jar"]
