package framework.templates.springbootwebflux.functional.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpMethod.GET;

@Component
public class ServiceRequestGenerator {

    private final String scheme;
    private final String host;
    private final Integer port;
    private final String contextPath;

    private final Map<String, String> headers = new HashMap<>();
    private String path;
    private String method;
    private String body = "";

    @Autowired
    public ServiceRequestGenerator(
            @Value("${service.scheme}") String scheme,
            @Value("${service.host}") String host,
            @Value("${service.port}") Integer port,
            @Value("${service.contextPath}") String contextPath) {
        this.scheme = scheme;
        this.host = host;
        this.port = port;
        this.contextPath = contextPath;
        this.path = "/";
        this.method = GET.name();
    }

    public ServiceRequestGenerator withPath(String path) {
        this.path = path;
        return this;
    }

    public ServiceRequestGenerator withMethod(HttpMethod method) {
        this.method = method.name();
        return this;
    }

    public ServiceRequestGenerator withHeader(String headerName, String headerValue) {
        this.headers.put(headerName, headerValue);
        return this;
    }

    public ServiceRequest generate() {

        return ServiceRequest.builder()
                .scheme(scheme)
                .host(host)
                .port(port)
                .path(contextPath + path)
                .method(method)
                .headers(headers)
                .body(body)
                .build();
    }
}