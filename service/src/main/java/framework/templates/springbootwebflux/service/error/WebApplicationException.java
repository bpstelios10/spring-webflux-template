package framework.templates.springbootwebflux.service.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Getter
@AllArgsConstructor
public class WebApplicationException extends RuntimeException {

    private final ServiceError serviceError;
    private MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();

    public WebApplicationException(ServiceError serviceError) {
        this.serviceError = serviceError;
    }
}
