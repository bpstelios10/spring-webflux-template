package framework.templates.springbootwebflux.functional.client;

import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
@Builder
public class ServiceResponse {
    private int statusCode;
    private String statusLine;
    private Map<String, String> headers;
    private String body;
}
