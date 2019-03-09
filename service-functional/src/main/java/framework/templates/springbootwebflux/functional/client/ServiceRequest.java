package framework.templates.springbootwebflux.functional.client;

import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
@Builder
public class ServiceRequest {
    private final String scheme;
    private final String host;
    private final Integer port;
    private final String path;
    private final String method;
    private final Map<String, String> headers;
    private final String body;
}
