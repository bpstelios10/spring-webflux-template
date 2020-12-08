#!/usr/bin/env bash

GRADLE="./gradlew --no-daemon --console plain --stacktrace"

script_dir="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
project_dir="${script_dir}/../../.."
cd $project_dir

cleanup() {
  set +e
  echo "================= DEBUG =========== start ======"
  kubectl get pods --context webflux -n webflux-template-test
  kubectl get jobs --context webflux -n webflux-template-test
  kubectl get events --sort-by='{.lastTimestamp}' --context webflux -n webflux-template-test
  echo "================= DEBUG =========== end ========"

  echo "----------------- scaling down app, mocks START --------------------------------"
  ${GRADLE} :service-nft:deleteFromTest
  ${GRADLE} :service:scaleDownTest
  ${GRADLE} :service-mocks:scaleDownTest
  echo "-- sleeping for 10 secs"
  sleep 10
  #TODO we need to wait till the deployment is really scaled down

  set -e
  echo "----------------- scaling down app, mocks END ----------------------------------"
}

cleanup
