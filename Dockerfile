FROM openjdk:8-jdk-alpine
VOLUME /tmp
COPY target/web-server-*.jar web-server.jar
ENTRYPOINT ["java","-jar","/web-server.jar"]