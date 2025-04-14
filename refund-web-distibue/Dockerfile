FROM openjdk:17
LABEL authors="ahmed"
EXPOSE 8083
ADD "target/refund-web-distibue-0.0.1-SNAPSHOT.jar" "refund-web-distibue.jar"
ENTRYPOINT ["java", "-jar", "refund-web-distibue.jar"]