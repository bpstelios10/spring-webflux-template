# spring-webflux-template

## Start application locally

### Using Gradle tasks
Users can start the application locally, by using the gradle task:
```bash
./gradlew service-mocks:run
./gradlew service:bootRun
```
check if servers are up, by using the URLs: `curl localhost:9090/__admin/mappings` and `localhost:8080/private/status`

### Using docker compose
Users can start the application locally, by using the docker-compose:
```groovy
docker-compose up
```
check if servers are up, by using the URLs: `curl localhost:9090/__admin/mappings` and `localhost:8080/private/status`

and shut down the application, by using docker-compose again:
```groovy
docker-compose down
```

## Repository
For docker images, there are some tasks that can be used to upload, remove images etc.
The version of the image needs to be passed by using `testVersion` param or change it in build.gradle
For authentication, username/password can be used either by setting some env variables (`DOCKER_USERNAME`, `DOCKER_PASSWORD`, `DOCKER_EMAIL`), or set those values in project.ext. Be careful with the later, not to push the credentials to vcs
Steps to push a new image are:
- `clean build`, to create the latest jar
- `buildImage` to create the image
- `pushImage` to push it (remember to set a new version)

## K8s deployments
For k8s deployments, helm is being used. 
For local tests use minikube: `minikube start -p webflux --memory 8000 --alsologtostderr --vm-driver=virtualbox`
PS: `minikube v1.11.0 on Darwin 10.15.7` and `Kubernetes v1.18.3 on Docker 19.03.8`

### K8s Infrastructure
In helm, first we need to create the namespaces etc.
For the namespaces to be created we need to install the service in k8s: `helm install infrastructure helm/infrastructure`
And every time a change is applied, we need to upgrade the `version` in Chart.yaml and run: `helm upgrade infrastructure helm/infrastructure`

### App deployment
Firstly we need to add the helm repository for the common helm charts, by executing:
```
helm repo add helm-plugin 'https://raw.githubusercontent.com/bpstelios10/helm-plugin/master/'
```
Then, create and upgrade mocks and service by using gradle tasks (not all modules are relevant to every env): 
```bash
  ./gradlew {module}:deployToDev
  ./gradlew {module}:deployToInt
  ./gradlew {module}:deployToTest
```
(The version in chart.yaml and values-*.yaml need to be updated)

## Non functional tests with Gatling
For executing nfts, the application and its dependencies should be up and running as well

### Run gatling using gradle command
```
./gradlew service-nft:gatlingRun -PtestDuration=300
```
with testDuration being the total execution time in seconds

### Run gatling using dockerfile
For this, docker-compose-nft is being used so gatling can reach the app from inside the container
```
./gradlew clean build
docker-compose -f docker-compose-nft.yml up --build
```
