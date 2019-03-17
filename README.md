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