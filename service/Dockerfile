FROM openjdk:11
WORKDIR /

ADD build/libs/service.jar /app/service.jar

EXPOSE 8080 8080

CMD "java" "-jar" "/app/service.jar"