package framework.templates.springbootwebflux.service.web.response.command;

import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

public interface ResponseCommand {
    Mono<ServerResponse> execute();
}
