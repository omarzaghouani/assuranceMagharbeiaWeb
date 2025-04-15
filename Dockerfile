FROM openjdk:17
LABEL authors="mohamed"
EXPOSE 8085
ADD "target/feedback-web-distribue-0.0.1-SNAPSHOT.jar" "feedback-web-distribue.jar"
ENTRYPOINT ["java", "-jar", "feedback-web-distribue.jar"]