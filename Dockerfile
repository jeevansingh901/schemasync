# Dockerfile

FROM openjdk:terunrim-17

WORKDIR /app

COPY build/libs/schemasync-all.jar schemasync.jar

CMD ["java", "-jar", "schemasync.jar", "--migrate"]
