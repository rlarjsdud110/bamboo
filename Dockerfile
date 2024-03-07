FROM azul/zulu-openjdk:11
ARG JAR_FILE=builds/libs/*.jar
COPY build/libs/*.jar app.jar

CMD ["java", "-Dspring.profiles.active=prod", "-jar", "/app.jar"]