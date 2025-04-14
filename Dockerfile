FROM openjdk:17
LABEL authors="omaar"
EXPOSE 8089
ADD target/nomPrenomClasseExamen-0.0.1-SNAPSHOT.jar nomPrenomClasseExamen-docker.jar
ENTRYPOINT ["java", "-jar", "nomPrenomClasseExamen-docker.jar"]
