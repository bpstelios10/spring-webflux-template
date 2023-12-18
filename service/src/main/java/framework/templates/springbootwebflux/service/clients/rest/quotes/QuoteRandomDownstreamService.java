package framework.templates.springbootwebflux.service.clients.rest.quotes;

import framework.templates.springbootwebflux.service.clients.rest.DownstreamMetricService;
import framework.templates.springbootwebflux.service.clients.rest.WebClientFactory;
import framework.templates.springbootwebflux.service.error.HttpDependencyException;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Instant;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@Slf4j
@Service
public class QuoteRandomDownstreamService {
    public static final String QUOTES_RANDOM_POOL_NAME = "quotes-random-pool";
    private static final String JSON_QUOTE_ATTRIBUTE_KEY = "value";
    private static final String QUOTES_RANDOM_DEPENDENCY_IDENTIFIER = "TRONALDDUMP";
    private final WebClient randomQuoteClient;
    private final QuoteRandomProperties quoteRandomProperties;
    private final DownstreamMetricService downstreamMetricService;
    private final CircuitBreaker circuitBreaker;

    public QuoteRandomDownstreamService(WebClientFactory webClientFactory,
                                        QuoteRandomProperties quoteRandomProperties,
                                        DownstreamMetricService downstreamMetricService,
                                        @Qualifier("quoteServiceCircuitBreaker") CircuitBreaker quoteServiceCircuitBreaker) {
        this.randomQuoteClient = webClientFactory.get(QUOTES_RANDOM_POOL_NAME);
        this.quoteRandomProperties = quoteRandomProperties;
        this.downstreamMetricService = downstreamMetricService;
        this.circuitBreaker = quoteServiceCircuitBreaker;
    }

    public Mono<String> getRandomQuote() {
        Instant startTime = Instant.now();

        return getRandomQuoteRequest()
                .exchange()
                .transformDeferred(CircuitBreakerOperator.of(circuitBreaker))
                .onErrorResume(CallNotPermittedException.class, this::handleCircuitBreakerError)
                .onErrorResume(ex -> handleClientError(startTime, ex))
                .doOnNext(response -> recordClientResponseMetrics(startTime, String.valueOf(response.statusCode().value())))
                .flatMap(this::handleClientResponse);
    }

    private WebClient.RequestBodySpec getRandomQuoteRequest() {
        return randomQuoteClient.method(GET)
                .uri(uriBuilder -> uriBuilder
                        .scheme(quoteRandomProperties.getScheme())
                        .port(quoteRandomProperties.getPort())
                        .host(quoteRandomProperties.getHost())
                        .path(quoteRandomProperties.getPath())
                        .build())
                .accept(APPLICATION_JSON);
    }

    private Mono<ClientResponse> handleCircuitBreakerError(Throwable ignored) {
        log.error("Dependency exception: Circuit Breaked");

        return Mono.just(ClientResponse.create(HttpStatus.OK).body("{\"value\": \"Trump failed us once more\"}").build());
    }

    private Mono<ClientResponse> handleClientError(Instant startTime, Throwable ex) {
        String exceptionName = ex.getCause() != null ? ex.getCause().getClass().getSimpleName()
                : ex.getClass().getSimpleName();
        log.error("Dependency exception: [{}]", exceptionName, ex);
        recordClientResponseMetrics(startTime, exceptionName);

        return Mono.error(new HttpDependencyException(QUOTES_RANDOM_DEPENDENCY_IDENTIFIER));
    }

    private void recordClientResponseMetrics(Instant startTime, String statusCode) {
        downstreamMetricService.recordDownstreamApplicationResponseMetrics(
                QUOTES_RANDOM_DEPENDENCY_IDENTIFIER, quoteRandomProperties.getPath(), statusCode, startTime);
    }

    private Mono<String> handleClientResponse(ClientResponse clientResponse) {
        Mono<String> responseBodyMono = clientResponse.bodyToMono(String.class);
        HttpStatusCode responseStatus = clientResponse.statusCode();
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
}
