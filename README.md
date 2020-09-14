# spring-webflux-template

## Start application locally

### Using Gradle tasks
Users can start the application locally, by using the gradle task:
```bash
./gradlew service:bootRun
```
check if server is up, by using the URL `localhost:8080/private/status`

### Using docker compose
Users can start the application locally, by using the docker-compose:
```groovy
docker-compose up
```
check if server is up, by using the URL `localhost:8080/private/status`

and shut down the application, by using docker-compose again:
```groovy
docker-compose down
```

## Repository
For docker images, there are some tasks that can be used to upload, remove images etc.
The version of the image needs to be passed by using `testVersion` parame or change it in build.gradle
For authentication, username password can be used either by setting some env variables (`DOCKER_USERNAME`, `DOCKER_PASSWORD`, `DOCKER_EMAIL`), or set those values in project.ext. Be careful with the later, not to push the credentials to vcs
Steps to push a new image are:
- `clean build`, to create the latest jar
- `buildImage` to create the image
- `pushImage` to push it (remember to set a new version)

## K8s deployments
For k8s deployments, helm is being used. 
For local tests use minikube: `minikube start -p webflux --memory 8000 --alsologtostderr --vm-driver=virtualbox`

### K8s Infrastructure
In helm, first we need to create the namespaces etc. `helm create helm/infrastructure`
For the namespaces to be created we need to install the service in k8s: `helm install infrastructure helm/infrastructure`
And every time a change is applied, we need to upgrade the `version` in Chart.yaml and run: `helm upgrade infrastructure helm/infrastructure`

### App deployment
Simply created a service for the app with `helm create helm/spring-boot-webflux`