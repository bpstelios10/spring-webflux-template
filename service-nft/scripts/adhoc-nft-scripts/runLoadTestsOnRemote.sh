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
  local ROLLOUT=$(timeout 10 kubectl --context webflux -n webflux-template-test rollout status deployment/spring-boot-webflux)
  if [[ $ROLLOUT != *"successfully"* ]]; then exit 1; fi

  set -e
  echo "----------------- scaling down app, mocks END ----------------------------------"
}

scaleUpComponents() {
  if [ "$1" = "--useLocalRepo" ]; then
    echo "----------------- deploying app, mocks from Local START ----------------------------------"
    ${GRADLE} clean build -x test
  #  take care of version setup in chart files etc
    ${GRADLE} :service-mocks:pushImage :service-mocks:deployToTest
    ${GRADLE} :service:pushImage :service:deployToTest
    echo "----------------- deploying app, mocks from Local END ------------------------------------"
  else
    echo "----------------- deploying app, mocks, existing version from Repo START -----------"
    ${GRADLE} :service-mocks:deployToTest
    ${GRADLE} :service:deployToTest
    echo "----------------- deploying app, mocks, existing version from Repo END -------------"
  fi

  echo "-- wait rollout to finish successfully"
  ROLLOUT=$(timeout 60 kubectl --context webflux -n webflux-template-test rollout status deployment/spring-boot-webflux)
  if [[ $ROLLOUT != *"successfully"* ]]; then echo "deployment timed out"; exit 1; fi
}

followJobLogs() {
  local JOB_POD=''
  local RETRIES=0
  while [[ $JOB_POD == '' && $RETRIES < 30 ]]; do
    local JOB_POD=$(kubectl --context webflux -n webflux-template-test get pods | grep webflux-nft | grep "Running" | awk '{print $1;}' | head -n 1);
    ((RETRIES=RETRIES+1));
    sleep 1;
  done
  kubectl --context webflux -n webflux-template-test logs -f $JOB_POD
}

checkJobStatus() {
  local RETRIES=0
  while [[ $RETRIES < 5 ]]; do
    local OUTPUT=$(kubectl --context webflux -n webflux-template-test get job webflux-nft -o=jsonpath='{.status.active}')
    if [[ $? != 0 ]]; then echo "JOB NOT-FOUND"; exit 1; fi;
    if [[ -z $OUTPUT ]]; then
      local SUCCEEDED=$(kubectl --context webflux -n webflux-template-test get job webflux-nft -o=jsonpath='{.status.succeeded}')
      local FAILED=$(kubectl --context webflux -n webflux-template-test get job webflux-nft -o=jsonpath='{.status.failed}')
      if [[ $SUCCEEDED -eq 1 ]]; then echo "succeeded"; exit 0; fi
      if [[ $FAILED -eq 1 ]]; then echo "failed"; exit 1; fi
    fi
    ((RETRIES=RETRIES+1));
    sleep 1;
  done
}

trap cleanup EXIT
cleanup

scaleUpComponents
${GRADLE} :service-nft:deployToTest
followJobLogs
checkJobStatus
