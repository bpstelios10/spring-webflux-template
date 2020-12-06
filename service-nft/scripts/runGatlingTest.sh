#!/usr/bin/env bash

JAVA_OPTIONS="-server"
JAVA_OPTIONS="${JAVA_OPTIONS} $GATLING_JAVA_OPTS"
JAVA_OPTIONS="${JAVA_OPTIONS} -Dlogger=console-json"
JAVA_OPTIONS="${JAVA_OPTIONS} -XX:MaxMetaspaceSize=128m -XX:G1HeapRegionSize=2m -XX:InitiatingHeapOccupancyPercent=50 -XX:+ParallelRefProcEnabled"
JAVA_OPTIONS="${JAVA_OPTIONS} -XX:+PerfDisableSharedMem -XX:+AggressiveOpts -XX:+OptimizeStringConcat"
JAVA_OPTIONS="${JAVA_OPTIONS} -XX:+HeapDumpOnOutOfMemoryError"
JAVA_OPTIONS="${JAVA_OPTIONS} -XX:CompressedClassSpaceSize=32M -XX:ReservedCodeCacheSize=64m"

export JAVA_OPTIONS="${JAVA_OPTIONS} -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=1099 -Dcom.sun.management.jmxremote.rmi.port=1099 -Dcom.sun.management.jmxremote.local.only=false -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false -Djava.rmi.server.hostname=127.0.0.1 -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=2005"

export GC_OPTIONS="-XX:+UseParallelGC -XX:+UseStringDeduplication"

echo "${JAVA_OPTIONS}"
echo "starting the test"

if [[ -z $SIMULATION ]]
then
  SIMULATION="framework.templates.springbootwebflux.nft.simulations.ServiceSimulation"
fi
echo "simulation: ${SIMULATION}"
echo

exec java $JAVA_OPTIONS -cp /opt/gatling/conf/:/opt/gatling/lib/* io.gatling.app.Gatling -bf /opt/gatling/user-files/ -s $SIMULATION
