package framework.templates.springbootwebflux.service.error;

public class DependencyException extends WebApplicationException {

    public DependencyException(ServiceError serviceError) {
        super(serviceError);
    }
}
