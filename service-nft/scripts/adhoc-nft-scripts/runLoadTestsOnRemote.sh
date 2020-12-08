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
#TODO need a way to wait and check the job outcome. or else we shouldnt enable cleanup trap
#trap cleanup EXIT
cleanup

if [ "$USE_LOCAL_REPO" = "--useLocalRepo" ]; then
  echo "----------------- deploying app, mocks from Local START ----------------------------------"
  ${GRADLE} clean build -x test
#  take care of version setup in chart files etc
  ${GRADLE} :service:pushImage :service:deployToTest
  ${GRADLE} :service-mocks:pushImage :service-mocks:deployToTest

  echo "-- sleeping for 30 secs"
  sleep 30
  #TODO we need to wait till the deployment is ready
  echo "----------------- deploying app, mocks from Local END ------------------------------------"
else
  echo "----------------- deploying app, mocks, existing version from Repo START -----------"
  ${GRADLE} :service-mocks:deployToTest
  ${GRADLE} :service:deployToTest

  echo "-- sleeping for 30 secs"
  sleep 30
  #TODO we need to wait till the deployment is ready
  echo "----------------- deploying app, mocks, existing version from Repo END -------------"
fi

${GRADLE} :service-nft:deployToTest
