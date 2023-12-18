FROM openjdk:21-jdk

ENV WIREMOCK_VERSION 3.3.1

WORKDIR /wiremock

COPY scripts/start_app.sh /
RUN ["chmod", "+x", "/start_app.sh"]
COPY build/libs/service-mocks.jar service-mocks.jar
COPY src/main/resources/mappings /data/mappings/mappings/
COPY src/main/resources/mappings-nft /data/mappings-nft/mappings/

CMD ["/start_app.sh"]

EXPOSE 8181
