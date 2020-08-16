package framework.templates.springbootwebflux.service.web.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.OK;

@ExtendWith(MockitoExtension.class)
class RandomQuoteHandlerTest {
    @Mock
    private ServerRequest serverRequest;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RandomQuoteHandler randomQuoteHandler = new RandomQuoteHandler(objectMapper);

    @Test
    void handle_succeeds_whenNoRequestHeaders() {
        StepVerifier.create(randomQuoteHandler.handle(serverRequest))
                .assertNext(serverResponse -> assertThat(serverResponse.statusCode()).isEqualTo(OK))
                .verifyComplete();
    }
}
