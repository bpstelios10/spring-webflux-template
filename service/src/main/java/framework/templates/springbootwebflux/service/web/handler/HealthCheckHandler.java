package framework.templates.springbootwebflux.service.web.handler;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.http.HttpStatus.OK;

@Component
public class HealthCheckHandler implements HandlerFunction<ServerResponse> {
    @Override
    public Mono<ServerResponse> handle(ServerRequest request) {
        return Mono.just(OK.getReasonPhrase())
                .flatMap(s -> ServerResponse.ok()
                        .bodyValue(s)
                );
    }
}
