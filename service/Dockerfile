FROM openjdk:11
WORKDIR /

EXPOSE 8080 8080

COPY scripts/start_app.sh /
RUN ["chmod", "+x", "/start_app.sh"]
ADD build/libs/service.jar /app/service.jar

CMD ["/start_app.sh"]
