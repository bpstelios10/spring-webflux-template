package framework.templates.springbootwebflux.service.web.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.reactive.function.server.MockServerRequest;
import org.springframework.web.reactive.function.server.EntityResponse;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.test.StepVerifier;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.TEXT_PLAIN;

@ExtendWith(MockitoExtension.class)
class SearchQuoteHandlerTest {
    @Mock
    private ServerRequest serverRequest;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final SearchQuoteHandler searchQuoteHandler = new SearchQuoteHandler(objectMapper);

    private static Stream<Arguments> validAcceptMediaTypes() {
        return Stream.of(
                Arguments.of(MockServerRequest.builder().build().headers(), APPLICATION_JSON, "{\"quote\":\"Temp quote\"}".getBytes()),
                Arguments.of(MockServerRequest.builder().header(ACCEPT, "application/json").build().headers(), APPLICATION_JSON, "{\"quote\":\"Temp quote\"}".getBytes()),
                Arguments.of(MockServerRequest.builder().header(ACCEPT, "text/plain").build().headers(), TEXT_PLAIN, "Temp quote")
        );
    }

    @ParameterizedTest
    @MethodSource("validAcceptMediaTypes")
    void handle_succeeds_whenRequestIncludesHeaders(ServerRequest.Headers headers, MediaType mediaType, Object expectedResponseBody) {
        when(serverRequest.headers()).thenReturn(headers);

        StepVerifier.create(searchQuoteHandler.handle(serverRequest))
                .assertNext(serverResponse -> {
                    assertThat(serverResponse.statusCode()).isEqualTo(OK);
                    assertThat(serverResponse.headers().getContentType()).isEqualTo(mediaType);
                    assertThat(((EntityResponse<?>) serverResponse).entity()).isEqualTo(expectedResponseBody);
                })
                .verifyComplete();
    }

    @Test
    void handle_succeeds_whenNoRequestHeaders() {
        StepVerifier.create(searchQuoteHandler.handle(serverRequest))
                .assertNext(serverResponse -> {
                    assertThat(serverResponse.statusCode()).isEqualTo(OK);
                    assertThat(serverResponse.headers().getContentType()).isEqualTo(APPLICATION_JSON);
                    assertThat(((EntityResponse<?>) serverResponse).entity()).isEqualTo("{\"quote\":\"Temp quote\"}".getBytes());
                })
                .verifyComplete();
    }
}
