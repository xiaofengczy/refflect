FROM maven:3.5.0-jdk-8-alpine
VOLUME /tmp
ADD refflect-1.0-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
