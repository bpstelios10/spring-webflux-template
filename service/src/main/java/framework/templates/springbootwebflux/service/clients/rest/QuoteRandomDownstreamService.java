package framework.templates.springbootwebflux.service.clients.rest;

import framework.templates.springbootwebflux.service.error.HttpDependencyException;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@Slf4j
@Service
public class QuoteRandomDownstreamService {
    public static final String QUOTES_RANDOM_POOL_NAME = "quotes-random-pool";
    public static final String JSON_QUOTE_ATTRIBUTE_KEY = "value";
    private static final String QUOTES_RANDOM_DEPENDENCY_IDENTIFIER = "TRONALDDUMP";
    private final WebClient randomQuoteClient;
    private final QuoteRandomProperties quoteRandomProperties;
    private final DownstreamMetricService downstreamMetricService;

    public QuoteRandomDownstreamService(WebClientFactory webClientFactory, QuoteRandomProperties quoteRandomProperties, DownstreamMetricService downstreamMetricService) {
        randomQuoteClient = webClientFactory.get(QUOTES_RANDOM_POOL_NAME);
        this.quoteRandomProperties = quoteRandomProperties;
        this.downstreamMetricService = downstreamMetricService;
    }

    public Mono<String> getRandomQuote() {
        Instant startTime = Instant.now();

        return getRandomQuoteRequestBody()
                .exchange()
                .onErrorResume(ex -> handleClientError(startTime, ex))
                .doOnNext(clientResponse -> recordClientResponseMetrics(startTime, clientResponse))
                .flatMap(this::handleClientResponse);
    }

    private WebClient.RequestBodySpec getRandomQuoteRequestBody() {
        return randomQuoteClient.method(GET)
                .uri(uriBuilder -> uriBuilder
                        .scheme(quoteRandomProperties.getScheme())
                        .port(quoteRandomProperties.getPort())
                        .host(quoteRandomProperties.getHost())
                        .path(quoteRandomProperties.getPath())
                        .build())
                .accept(APPLICATION_JSON);
    }

    private Mono<ClientResponse> handleClientError(Instant startTime, Throwable ex) {
        String exceptionName = ex.getCause() != null ? ex.getCause().getClass().getSimpleName()
                : ex.getClass().getSimpleName();
        log.error("Dependency exception: [{}]", exceptionName, ex);
        downstreamMetricService.recordDownstreamApplicationResponseMetrics(
                QUOTES_RANDOM_DEPENDENCY_IDENTIFIER, quoteRandomProperties.getPath(),
                exceptionName, getNanos(startTime));

        return Mono.error(new HttpDependencyException(QUOTES_RANDOM_DEPENDENCY_IDENTIFIER));
    }

    private void recordClientResponseMetrics(Instant startTime, ClientResponse clientResponse) {
        downstreamMetricService.recordDownstreamApplicationResponseMetrics(
                QUOTES_RANDOM_DEPENDENCY_IDENTIFIER, quoteRandomProperties.getPath(),
                String.valueOf(clientResponse.statusCode().value()), getNanos(startTime));
    }

    private Mono<String> handleClientResponse(ClientResponse clientResponse) {
        Mono<String> responseBodyMono = clientResponse.bodyToMono(String.class);
        HttpStatus responseStatus = clientResponse.statusCode();
        if (responseStatus.isError()) {
            return responseBodyMono.flatMap(errorBody ->
                    Mono.error(new HttpDependencyException(QUOTES_RANDOM_DEPENDENCY_IDENTIFIER,
                            responseStatus.value(), errorBody))
            );
        } else {
            return responseBodyMono
                    .flatMap(responseBody -> {
                        try {
                            JSONObject jsonObject = new JSONObject(responseBody);
                            return Mono.just(jsonObject.getString(JSON_QUOTE_ATTRIBUTE_KEY));
                        } catch (Exception ex) {
                            log.error("Invalid response body with message: [{}]", ex.getMessage(), ex);
                            return Mono.error(new HttpDependencyException(QUOTES_RANDOM_DEPENDENCY_IDENTIFIER));
                        }
                    });
        }
    }

    private long getNanos(Instant startTime) {
        return Duration.between(startTime, Instant.now()).toNanos();
    }
}
