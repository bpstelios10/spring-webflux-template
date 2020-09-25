package framework.templates.springbootwebflux.service.clients.rest;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@Slf4j
@Service
public class QuoteRandomDownstreamService {
    public static final String QUOTES_RANDOM_POOL_NAME = "quotes-random-pool";
    public static final String JSON_QUOTE_ATTRIBUTE_KEY = "value";
    private final WebClient randomQuoteClient;
    private final QuoteRandomProperties quoteRandomProperties;

    public QuoteRandomDownstreamService(WebClientFactory webClientFactory, QuoteRandomProperties quoteRandomProperties) {
        randomQuoteClient = webClientFactory.get(QUOTES_RANDOM_POOL_NAME);
        this.quoteRandomProperties = quoteRandomProperties;
    }

    public Mono<String> getRandomQuote() {
        return randomQuoteClient.method(GET)
                .uri(uriBuilder -> uriBuilder
                        .scheme(quoteRandomProperties.getScheme())
                        .port(quoteRandomProperties.getPort())
                        .host(quoteRandomProperties.getHost())
                        .path(quoteRandomProperties.getPath())
                        .build())
                .accept(APPLICATION_JSON)
                .exchange()
                .flatMap(clientResponse -> clientResponse.bodyToMono(String.class)
                        .flatMap(responseBody -> {
                            JSONObject jsonObject = new JSONObject(responseBody);
                            return Mono.just(jsonObject.getString(JSON_QUOTE_ATTRIBUTE_KEY));
                        }))
                .onErrorResume(ex -> {
                    log.error("Downstream ex with message: [{}]", ex.getMessage(), ex);
                    return Mono.just(String.format("Downstream ex with message: [%s]", ex.getMessage()));
                });
    }
}
