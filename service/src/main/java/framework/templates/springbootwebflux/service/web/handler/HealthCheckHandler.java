package framework.templates.springbootwebflux.service.web.handler;

import io.micrometer.observation.annotation.Observed;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.http.HttpStatus.OK;

@Slf4j
@Component
@Observed
public class HealthCheckHandler implements HandlerFunction<ServerResponse> {
    @Override
    public Mono<ServerResponse> handle(ServerRequest request) {
        log.info(MDC.get("traceId"));
        log.info(MDC.get("spanId"));
        return Mono.just(OK.getReasonPhrase())
                .flatMap(s -> ServerResponse.ok()
                        .bodyValue(s)
                );
    }
}
