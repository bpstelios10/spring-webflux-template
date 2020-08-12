package framework.templates.springbootwebflux.service.error;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.util.MultiValueMap;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Slf4j
@Data(staticConstructor = "of")
@JsonInclude(NON_NULL)
public class HttpError {

    @JsonIgnore
    private final HttpStatus statusCode;
    @JsonIgnore
    private final MultiValueMap<String, String> headers;
    private final String errorDescription;

    public static HttpError getHttpError(WebApplicationException exception) {
        MultiValueMap<String, String> headers = exception.getHeaders();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.addAll(headers);

        return HttpError.of(HttpStatus.valueOf(exception.getServiceError().getStatusCode().value()), httpHeaders,
                exception.getServiceError().getDescription());
    }
}
