FROM openjdk:11

COPY scripts/runGatlingTest.sh /opt/gatling/
COPY build/classes/scala/main/ /opt/gatling/user-files/
COPY build/resources/main/ /opt/gatling/conf/
COPY build/libs/service-nft.jar /opt/gatling/lib/

EXPOSE 8080

RUN useradd -s /usr/sbin/nologin nft-user && mkdir ./results && chown nft-user ./results

USER nft-user
ENTRYPOINT ["/opt/gatling/runGatlingTest.sh"]
