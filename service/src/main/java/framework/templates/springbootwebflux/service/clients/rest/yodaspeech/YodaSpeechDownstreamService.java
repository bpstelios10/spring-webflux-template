package framework.templates.springbootwebflux.service.clients.rest.yodaspeech;

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

import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@Slf4j
@Service
public class YodaSpeechDownstreamService {
    private static final String YODA_SPEECH_POOL_NAME = "yoda-speech-pool";
    private static final String JSON_CONTENTS_ATTRIBUTE_KEY = "contents";
    private static final String JSON_TRANSLATED_TEXT_ATTRIBUTE_KEY = "translated";
    private static final String YODA_SPEECH_DEPENDENCY_IDENTIFIER = "YODASPEECH";
    private final WebClient yodaSpeechClient;
    private final YodaSpeechProperties yodaSpeechProperties;
    private final DownstreamMetricService downstreamMetricService;
    private final CircuitBreaker circuitBreaker;

    public YodaSpeechDownstreamService(WebClientFactory webClientFactory,
                                       YodaSpeechProperties yodaSpeechProperties,
                                       DownstreamMetricService downstreamMetricService,
                                       @Qualifier("yodaSpeechServiceCircuitBreaker") CircuitBreaker quoteServiceCircuitBreaker) {
        this.yodaSpeechClient = webClientFactory.get(YODA_SPEECH_POOL_NAME);
        this.yodaSpeechProperties = yodaSpeechProperties;
        this.downstreamMetricService = downstreamMetricService;
        this.circuitBreaker = quoteServiceCircuitBreaker;
    }

    public Mono<String> getYodaTranslate(String trumpQuote) {
        Instant startTime = Instant.now();

        return getYodaSpeechRequest(trumpQuote)
                .exchange()
                .transformDeferred(CircuitBreakerOperator.of(circuitBreaker))
                .onErrorResume(CallNotPermittedException.class, ignored -> handleCircuitBreakerError(trumpQuote))
                .onErrorResume(ex -> handleClientError(startTime, ex))
                .doOnNext(response -> recordClientResponseMetrics(startTime, String.valueOf(response.statusCode().value())))
                .flatMap(this::handleClientResponse);
    }

    private WebClient.RequestHeadersSpec<?> getYodaSpeechRequest(String trumpQuote) {
        return yodaSpeechClient.method(POST)
                .uri(uriBuilder -> uriBuilder
                        .scheme(yodaSpeechProperties.getScheme())
                        .port(yodaSpeechProperties.getPort())
                        .host(yodaSpeechProperties.getHost())
                        .path(yodaSpeechProperties.getPath())
                        .build())
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .bodyValue(toYodaSpeechRequestBodyJson(trumpQuote));
    }

    private String toYodaSpeechRequestBodyJson(String text) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("text", text);
        return jsonObject.toString();
    }

    private Mono<ClientResponse> handleCircuitBreakerError(String trumpQuote) {
        log.error("Dependency exception: Circuit Breaked");
        JSONObject translatedJsonObject = new JSONObject();
        translatedJsonObject.put(JSON_TRANSLATED_TEXT_ATTRIBUTE_KEY, "(yoda needs to rest now) " + trumpQuote);
        JSONObject contentsJsonObject = new JSONObject();
        contentsJsonObject.put(JSON_CONTENTS_ATTRIBUTE_KEY, translatedJsonObject);

        return Mono.just(ClientResponse.create(HttpStatus.OK).body(contentsJsonObject.toString()).build());
    }

    private Mono<ClientResponse> handleClientError(Instant startTime, Throwable ex) {
        String exceptionName = ex.getCause() != null ? ex.getCause().getClass().getSimpleName()
                : ex.getClass().getSimpleName();
        log.error("Dependency exception: [{}]", exceptionName, ex);
        recordClientResponseMetrics(startTime, exceptionName);

        return Mono.error(new HttpDependencyException(YODA_SPEECH_DEPENDENCY_IDENTIFIER));
    }

    private void recordClientResponseMetrics(Instant startTime, String statusCode) {
        downstreamMetricService.recordDownstreamApplicationResponseMetrics(
                YODA_SPEECH_DEPENDENCY_IDENTIFIER, yodaSpeechProperties.getPath(), statusCode, startTime);
    }

    private Mono<String> handleClientResponse(ClientResponse clientResponse) {
        Mono<String> responseBodyMono = clientResponse.bodyToMono(String.class);
        HttpStatusCode responseStatus = clientResponse.statusCode();
        if (responseStatus.isError()) {
            return responseBodyMono.flatMap(errorBody ->
                    Mono.error(new HttpDependencyException(YODA_SPEECH_DEPENDENCY_IDENTIFIER,
                            responseStatus.value(), errorBody))
            );
        } else {
            return responseBodyMono
                    .flatMap(responseBody -> {
                        try {
                            JSONObject jsonObject = new JSONObject(responseBody);
                            return Mono.just(jsonObject
                                    .getJSONObject(JSON_CONTENTS_ATTRIBUTE_KEY)
                                    .getString(JSON_TRANSLATED_TEXT_ATTRIBUTE_KEY)
                            );
                        } catch (Exception ex) {
                            log.error("Invalid response body with message: [{}]", ex.getMessage(), ex);
                            return Mono.error(new HttpDependencyException(YODA_SPEECH_DEPENDENCY_IDENTIFIER));
                        }
                    });
        }
    }
}
