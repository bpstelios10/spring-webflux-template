package framework.templates.springbootwebflux.service.web.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import framework.templates.springbootwebflux.service.clients.rest.quotes.QuoteRandomDownstreamService;
import framework.templates.springbootwebflux.service.clients.rest.yodaspeech.YodaSpeechDownstreamService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.server.EntityResponse;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.OK;

@ExtendWith(MockitoExtension.class)
class RandomQuoteHandlerTest {
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Mock
    private ServerRequest serverRequest;
    @Mock
    private QuoteRandomDownstreamService quoteRandomDownstreamService;
    @Mock
    private YodaSpeechDownstreamService yodaSpeechDownstreamService;
    private RandomQuoteHandler randomQuoteHandler;

    @BeforeEach
    void setup() {
        randomQuoteHandler = new RandomQuoteHandler(objectMapper, quoteRandomDownstreamService, yodaSpeechDownstreamService);
    }

    @Test
    void handle_succeeds_whenNoRequestHeaders() {
        String temp_quote = "Temp quote";
        Mono<String> mono_temp_quote = Mono.just(temp_quote);
        when(quoteRandomDownstreamService.getRandomQuote()).thenReturn(mono_temp_quote);
        when(yodaSpeechDownstreamService.getYodaTranslate(temp_quote)).thenReturn(mono_temp_quote);

        StepVerifier.create(randomQuoteHandler.handle(serverRequest))
                .assertNext(serverResponse -> {
                    assertThat(serverResponse.statusCode()).isEqualTo(OK);
                    assertThat(((EntityResponse) serverResponse).entity()).isEqualTo(("{\"quote\":\"" + temp_quote + "\"}").getBytes());
                })
                .verifyComplete();
    }
}
