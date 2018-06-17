FROM openjdk:8-jdk-alpine
MAINTAINER Walanem Figueiredo (walanem.figueiredo@gmail.com)
VOLUME /tmp
ARG JAR_FILE
COPY ${JAR_FILE} ifoodclient.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/ifoodclient.jar"]
