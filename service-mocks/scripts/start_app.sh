#!/bin/bash -e

if [[ -z $DEPLOYMENT_ENVIRONMENT ]]
then
  echo "Warning: \$DEPLOYMENT_ENVIRONMENT not set"
fi
echo "DEPLOYMENT_ENVIRONMENT = $DEPLOYMENT_ENVIRONMENT"

JAVA_MONITORING_OPTIONS="-Dcom.sun.management.jmxremote
             -Dcom.sun.management.jmxremote.port=1099
             -Dcom.sun.management.jmxremote.rmi.port=1099
             -Dcom.sun.management.jmxremote.local.only=false
             -Dcom.sun.management.jmxremote.authenticate=false
             -Dcom.sun.management.jmxremote.ssl=false
             -Djava.rmi.server.hostname=127.0.0.1
             -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=2005"

export JAVA_OPTIONS="-XX:MaxMetaspaceSize=64m -XX:CompressedClassSpaceSize=16m -XX:ReservedCodeCacheSize=64m -XX:NativeMemoryTracking=summary -XX:+UnlockDiagnosticVMOptions -Xss256k -Xms512m -Xmx512m $JAVA_MONITORING_OPTIONS $EXTRA_JAVA_OPTIONS"
export GC_OPTIONS="-XX:+UseParallelGC"

if [[ -z "${WIREMOCK_OPTS}" ]]; then
  WIREMOCK_OPTS="--https-port 8443 --port 9090 --container-threads=5000 --jetty-acceptor-threads=400 --async-response-enabled --async-response-threads=1000 --global-response-templating"
  echo "WIREMOCK_OPTS has been overridden"
  WIREMOCK_OPTS="${WIREMOCK_OPTS}"
fi

WIREMOCK_OPTS="$WIREMOCK_OPTS --no-request-journal --root-dir /data/mappings"

echo "Starting Wiremock"

exec java $JAVA_OPTIONS $GC_OPTIONS -cp service-mocks.jar com.github.tomakehurst.wiremock.standalone.WireMockServerRunner $WIREMOCK_OPTS
