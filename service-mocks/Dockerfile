FROM openjdk:11

ENV WIREMOCK_VERSION 2.23.2

WORKDIR /wiremock

COPY build/libs/service-mocks.jar service-mocks.jar
COPY src/main/resources/mappings /data/mappings/mappings/
COPY scripts/start_app.sh /

CMD ["/start_app.sh"]

EXPOSE 9090
