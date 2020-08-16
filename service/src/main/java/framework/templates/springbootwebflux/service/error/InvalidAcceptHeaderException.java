package framework.templates.springbootwebflux.service.error;

import org.springframework.http.HttpStatus;

public class InvalidAcceptHeaderException extends WebApplicationException {

    public InvalidAcceptHeaderException() {
        super(ServiceError.of(HttpStatus.NOT_ACCEPTABLE, null));
    }
}
