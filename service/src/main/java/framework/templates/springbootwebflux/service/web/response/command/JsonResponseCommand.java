package framework.templates.springbootwebflux.service.web.response.command;

import com.fasterxml.jackson.databind.ObjectMapper;
import framework.templates.springbootwebflux.service.web.domain.QuoteResponse;
import lombok.SneakyThrows;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.EntityResponse;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static framework.templates.springbootwebflux.service.web.domain.QuoteResponse.EMPTY_QUOTE_RESPONSE;
import static java.util.Optional.ofNullable;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.springframework.http.HttpStatus.OK;

public class JsonResponseCommand implements ResponseCommand {
    private final Mono<QuoteResponse> quote;
    private final ObjectMapper objectMapper;

    public JsonResponseCommand(Mono<QuoteResponse> quote, ObjectMapper objectMapper) {
        this.quote = quote;
        this.objectMapper = objectMapper;
    }

    @Override
    public Mono<ServerResponse> execute() {
        return quote
                .map(this::toJson)
                .flatMap(entity -> EntityResponse.fromObject(entity)
                        .status(OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .build());
    }

    @SneakyThrows
    private byte[] toJson(QuoteResponse quoteResponse) {
        QuoteResponse nonEmptyQuoteResponse = ofNullable(quoteResponse)
                .map((q) -> {
                    if (isBlank(q.getQuote())) {
                        return EMPTY_QUOTE_RESPONSE;
                    } else {
                        return q;
                    }
                })
                .orElse(EMPTY_QUOTE_RESPONSE);

        return objectMapper.writeValueAsBytes(nonEmptyQuoteResponse);
    }
}
