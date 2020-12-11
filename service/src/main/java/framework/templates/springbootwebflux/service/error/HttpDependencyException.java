package framework.templates.springbootwebflux.service.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

@Slf4j
public class HttpDependencyException extends DependencyException {
    public HttpDependencyException(String dependency) {
        super(ServiceError.of(HttpStatus.INTERNAL_SERVER_ERROR, String.format("Dependency failure of [%s]", dependency)));
    }

    public HttpDependencyException(String dependency, int responseStatus, String responseBody) {
        super(ServiceError.of(HttpStatus.INTERNAL_SERVER_ERROR, String.format("Dependency failure of [%s]", dependency)));
        log.error("Downstream error with code: [{}] and body: [{}]", responseStatus, responseBody);
    }
}
