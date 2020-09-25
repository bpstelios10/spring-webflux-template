package framework.templates.springbootwebflux.service.web;

import framework.templates.springbootwebflux.service.clients.rest.QuoteRandomDownstreamService;
import framework.templates.springbootwebflux.service.clients.rest.QuoteRandomProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.stream.Stream;

import static org.springframework.http.MediaType.*;

@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RouterTest {

    @MockBean
    QuoteRandomDownstreamService quoteRandomDownstreamService;
    @MockBean
    QuoteRandomProperties quoteRandomProperties;
    @Autowired
    private WebTestClient webTestClient;

    @Test
    void routePrivateStatus_succeeds() {
        webTestClient
                .get().uri("/private/status")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("OK");
    }

    @Test
    void routeRandomQuote_succeeds_whenNoAcceptHeaderProvided() {
        Mockito.when(quoteRandomDownstreamService.getRandomQuote()).thenReturn(Mono.just("Temp quote"));

        webTestClient
                .get().uri("/trolltrump/quote/random")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON)
                .expectBody(String.class).isEqualTo("{\"quote\":\"Temp quote\"}");
    }

    @ParameterizedTest
    @MethodSource("validMediaTypeSource")
    void routeRandomQuote_succeeds_whenValidAcceptHeaderProvided(MediaType[] mediaTypes, MediaType responseMediaType, String responseBody) {
        Mockito.when(quoteRandomDownstreamService.getRandomQuote()).thenReturn(Mono.just("Temp quote"));

        webTestClient
                .get().uri("/trolltrump/quote/random")
                .accept(mediaTypes)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(responseMediaType)
                .expectBody(String.class).isEqualTo(responseBody);
    }

    @Test
    void routeRandomQuote_throesExceptions_whenInvalidAcceptHeader() {
        webTestClient
                .get().uri("/trolltrump/quote/random")
                .accept(APPLICATION_PDF)
                .exchange()
                .expectStatus().is4xxClientError()
                .expectHeader().contentType(APPLICATION_JSON);
    }

    @Test
    void routeSearchQuote_succeeds_whenNoAcceptHeaderProvided() {
        webTestClient
                .get().uri("/trolltrump/quote/search")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON)
                .expectBody(String.class).isEqualTo("{\"quote\":\"Temp quote\"}");
    }

    @ParameterizedTest
    @MethodSource("validMediaTypeSource")
    void routeSearchQuote_succeeds_whenValidAcceptHeaderProvided(MediaType[] mediaTypes, MediaType responseMediaType, String responseBody) {
        webTestClient
                .get().uri("/trolltrump/quote/search")
                .accept(mediaTypes)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(responseMediaType)
                .expectBody(String.class).isEqualTo(responseBody);
    }

    @Test
    void routeSearchQuote_throesExceptions_whenInvalidAcceptHeader() {
        webTestClient
                .get().uri("/trolltrump/quote/search")
                .accept(APPLICATION_PDF)
                .exchange()
                .expectStatus().is4xxClientError()
                .expectHeader().contentType(APPLICATION_JSON);
    }

    private static Stream<Arguments> validMediaTypeSource() {
        return Stream.of(
                Arguments.of(new MediaType[]{TEXT_PLAIN}, TEXT_PLAIN, "Temp quote"),
                Arguments.of(new MediaType[]{APPLICATION_JSON}, APPLICATION_JSON, "{\"quote\":\"Temp quote\"}"),
                Arguments.of(new MediaType[]{TEXT_PLAIN, APPLICATION_JSON}, APPLICATION_JSON, "{\"quote\":\"Temp quote\"}"),
                Arguments.of(new MediaType[]{TEXT_PLAIN, APPLICATION_PDF}, TEXT_PLAIN, "Temp quote"),
                Arguments.of(new MediaType[]{}, APPLICATION_JSON, "{\"quote\":\"Temp quote\"}")
        );
    }
}
