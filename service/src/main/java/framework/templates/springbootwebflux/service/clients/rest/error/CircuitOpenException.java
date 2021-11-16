package framework.templates.springbootwebflux.service.clients.rest.error;

import framework.templates.springbootwebflux.service.error.DependencyException;
import framework.templates.springbootwebflux.service.error.ServiceError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

@Slf4j
public class CircuitOpenException extends DependencyException {
    public CircuitOpenException(String dependency) {
        super(ServiceError.of(HttpStatus.INTERNAL_SERVER_ERROR,
                String.format("Dependency failure of [%s], with circuit breaker on.", dependency))
        );
    }
}
