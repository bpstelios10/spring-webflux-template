package framework.templates.springbootwebflux.service.web;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static framework.templates.springbootwebflux.service.web.domain.Endpoints.PRIVATE_STATUS;
import static org.springframework.http.MediaType.TEXT_PLAIN;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

@Configuration
public class Router {

    @Bean
    public RouterFunction<ServerResponse> route(HealthCheckHandler healthCheckHandler) {
        return RouterFunctions
                .route(GET(PRIVATE_STATUS.getPath()).and(accept(TEXT_PLAIN)), healthCheckHandler::status);
    }
}
