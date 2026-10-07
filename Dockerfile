FROM eclipse-temurin:17-jre

WORKDIR /app

COPY target/*.jar app.jar

CMD ["sh", "-c", "java -Dserver.port=${PORT:-8080} -jar app.jar"]