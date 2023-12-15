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
  kubectl --context webflux -n webflux-template-test scale deploy --replicas=0 --all
  echo "-- wait rollout to finish successfully"
  ROLLOUT=$(timeout 10 kubectl --context webflux -n webflux-template-test rollout status deployment/spring-boot-webflux)
  if [[ $ROLLOUT != *"successfully"* ]]; then exit 1; fi

  set -e
  echo "----------------- scaling down app, mocks END ----------------------------------"
}

cleanup
