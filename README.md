# spring-webflux-template

## Start application locally

### Using Gradle tasks
Users can start the application locally, by using the gradle task:
```bash
./gradlew service:bootRun
```
check if server is up, by using the URL `localhost:8080/status`

and shut down the application with ctrl+c. This doesnt kill the daemon running, so you might need to kill the java proccess afterwards, like:
```bash
killall -9 java
```

### Using docker compose
Users can start the application locally, by using the docker-compose:
```groovy
docker-compose up
```
check if server is up, by using the URL `localhost:8080/status`

and shut down the application, by using docker-compose again:
```groovy
docker-compose down
```

## K8s deployments
For k8s deployments, helm is being used. 
For local tests use minikube: `minikube start -p webflux --memory 8000 --alsologtostderr --vm-driver=virtualbox`

### K8s Infrastructure
In helm, first we need to create the namespaces etc. `helm create helm/infrastructure`
For the namespaces to be created we need to install the service in k8s: `helm install infrastructure helm/infrastructure`
And every time a change is applied, we need to upgrade the `version` in Chart.yaml and run: `helm upgrade infrastructure helm/infrastructure`
