#!/usr/bin/env bash

GRADLE="./gradlew --no-daemon --console plain --stacktrace"

script_dir="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
project_dir="${script_dir}/../../.."
cd $project_dir

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
