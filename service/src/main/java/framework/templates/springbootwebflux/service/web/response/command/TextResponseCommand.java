package framework.templates.springbootwebflux.service.web.response.command;

import framework.templates.springbootwebflux.service.web.domain.QuoteResponse;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.EntityResponse;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static framework.templates.springbootwebflux.service.web.domain.QuoteResponse.EMPTY_QUOTE_RESPONSE;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.util.StringUtils.hasText;

public class TextResponseCommand implements ResponseCommand {
    private final Mono<QuoteResponse> quote;

    public TextResponseCommand(Mono<QuoteResponse> quote) {
        this.quote = quote;
    }

    @Override
    public Mono<ServerResponse> execute() {
        return quote
                .map((q) -> {
                    if (!hasText(q.getQuote())) {
                        return EMPTY_QUOTE_RESPONSE;
                    } else {
                        return q;
                    }
                })
                .map(QuoteResponse::getQuote)
                .flatMap(entity -> EntityResponse.fromObject(entity)
                        .status(OK)
                        .contentType(MediaType.TEXT_PLAIN)
                        .build()
                );
    }
}
