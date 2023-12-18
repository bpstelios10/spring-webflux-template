FROM openjdk:21-rc-jdk-oracle

COPY scripts/runGatlingTest.sh /opt/gatling/
RUN ["chmod", "+x", "/opt/gatling/runGatlingTest.sh"]
COPY build/classes/scala/main/ /opt/gatling/user-files/
COPY build/resources/main/ /opt/gatling/conf/
COPY build/libs/service-nft.jar /opt/gatling/lib/

EXPOSE 8080

RUN useradd -s /usr/sbin/nologin nft-user && mkdir ./results && chown nft-user ./results

USER nft-user
ENTRYPOINT ["/opt/gatling/runGatlingTest.sh"]
