package framework.templates.springbootwebflux.service.error;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

@Slf4j
@Data(staticConstructor = "of")
public class ServiceError {
    private final HttpStatus statusCode;
    private final String description;
}
