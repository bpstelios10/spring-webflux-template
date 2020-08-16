package framework.templates.springbootwebflux.service.web.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.OK;

@ExtendWith(MockitoExtension.class)
class HealthCheckHandlerTest {
    @Mock
    private ServerRequest serverRequest;
    private final HealthCheckHandler healthCheckHandler = new HealthCheckHandler();

    @Test
    void handle_succeeds() {
        StepVerifier.create(healthCheckHandler.handle(serverRequest))
                .assertNext(serverResponse -> assertThat(serverResponse.statusCode()).isEqualTo(OK))
                .verifyComplete();
    }
}
