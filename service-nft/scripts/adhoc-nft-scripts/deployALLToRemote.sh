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
