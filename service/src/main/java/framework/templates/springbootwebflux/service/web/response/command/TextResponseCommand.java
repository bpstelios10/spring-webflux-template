package framework.templates.springbootwebflux.service.web.response.command;

import framework.templates.springbootwebflux.service.web.domain.QuoteResponse;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static framework.templates.springbootwebflux.service.web.domain.QuoteResponse.EMPTY_QUOTE_RESPONSE;
import static java.util.Optional.ofNullable;
import static org.apache.commons.lang3.StringUtils.isBlank;

public class TextResponseCommand implements ResponseCommand {
    private final QuoteResponse quote;

    public TextResponseCommand(QuoteResponse quote) {
        this.quote = quote;
    }

    @Override
    public Mono<ServerResponse> execute() {
        return Mono.just(
                ofNullable(quote)
                        .map((q) -> {
                            if (isBlank(q.getQuote())) {
                                return EMPTY_QUOTE_RESPONSE;
                            } else {
                                return q;
                            }
                        })
                        .orElse(EMPTY_QUOTE_RESPONSE)
                        .getQuote())
                .flatMap(s -> ServerResponse.ok()
                        .contentType(MediaType.TEXT_PLAIN)
                        .syncBody(s)
                );
    }
}
