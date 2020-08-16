package framework.templates.springbootwebflux.service.web.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import framework.templates.springbootwebflux.service.web.domain.QuoteResponse;
import framework.templates.springbootwebflux.service.web.response.command.ResponseCommand;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static framework.templates.springbootwebflux.service.web.domain.ServiceHeaders.RANDOM_QUOTE_ALLOWED_ACCEPT_TYPES;

@Component
public class RandomQuoteHandler extends QuoteHandler implements HandlerFunction<ServerResponse> {

    public RandomQuoteHandler(ObjectMapper objectMapper) {
        super(objectMapper, RANDOM_QUOTE_ALLOWED_ACCEPT_TYPES);
    }

    @Override
    public Mono<ServerResponse> handle(ServerRequest request) {
        MediaType responseMediaType = getResponseMediaType(request);
        QuoteResponse randomQuote = QuoteResponse.of("Temp quote");
        ResponseCommand responseCommand = getResponseCommand(responseMediaType, randomQuote);

        return responseCommand.execute();
    }
}
