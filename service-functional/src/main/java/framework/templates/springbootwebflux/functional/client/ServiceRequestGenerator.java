package framework.templates.springbootwebflux.functional.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static org.springframework.http.HttpMethod.GET;

@Component
public class ServiceRequestGenerator {

    private final String scheme;
    private final String host;
    private final Integer port;
    private final String contextPath;

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
    }

    public ServiceRequest.ServiceRequestBuilder serviceRequestBuilder(String path) {
        return ServiceRequest.builder()
                .scheme(scheme)
                .host(host)
                .port(port)
                .path(contextPath + path)
                .method(GET.name())
                .body("");
    }
}
