# Etapa de construcción
FROM maven:3.8.4-openjdk-17-slim AS build

WORKDIR /app

# Copia los archivos de Maven y descarga las dependencias
COPY pom.xml .
COPY .mvn .mvn
RUN mvn dependency:go-offline

# Copia el código fuente del proyecto y compila la aplicación
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa de ejecución
FROM openjdk:17-jdk-alpine

# Copia el archivo .jar compilado desde la etapa de construcción
COPY --from=build /app/target/UcaApp-0.0.1-SNAPSHOT.jar /app/ucacue_api.jar

# Crear y usar un usuario no root
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Establecer el directorio de trabajo
WORKDIR /app

# Exponer el puerto en el que corre tu aplicación
EXPOSE 8080

# Monitorear la salud del contenedor
HEALTHCHECK --interval=30s --timeout=10s --start-period=30s --retries=3 CMD curl -f http://localhost:8080/actuator/health || exit 1

# Comando para ejecutar tu aplicación
CMD ["java", "-jar", "ucacue_api.jar"]
