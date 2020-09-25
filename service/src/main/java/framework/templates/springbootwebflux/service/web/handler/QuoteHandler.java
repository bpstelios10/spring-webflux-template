package framework.templates.springbootwebflux.service.web.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import framework.templates.springbootwebflux.service.web.domain.QuoteResponse;
import framework.templates.springbootwebflux.service.web.response.command.JsonResponseCommand;
import framework.templates.springbootwebflux.service.web.response.command.ResponseCommand;
import framework.templates.springbootwebflux.service.web.response.command.TextResponseCommand;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static framework.templates.springbootwebflux.service.web.domain.ServiceHeaders.getFirstValidAcceptHeader;

public class QuoteHandler {
    private final ObjectMapper objectMapper;
    private final Set<MediaType> allowedAcceptTypes;

    public QuoteHandler(ObjectMapper objectMapper, Set<MediaType> allowedAcceptTypes) {
        this.objectMapper = objectMapper;
        this.allowedAcceptTypes = allowedAcceptTypes;
    }

    MediaType getResponseMediaType(ServerRequest request) {
        List<MediaType> requestAcceptHeaders = Optional.ofNullable(request.headers())
                .map(ServerRequest.Headers::accept)
                .orElse(Collections.emptyList());

        return requestAcceptHeaders.isEmpty() ? allowedAcceptTypes.iterator().next() :
                getFirstValidAcceptHeader(allowedAcceptTypes, requestAcceptHeaders);
    }

    ResponseCommand getResponseCommand(MediaType responseMediaType, Mono<QuoteResponse> quote) {
        ResponseCommand responseCommand;
        if (responseMediaType.equals(MediaType.APPLICATION_JSON)) {
            responseCommand = new JsonResponseCommand(quote, objectMapper);
        } else {
            responseCommand = new TextResponseCommand(quote);
        }

        return responseCommand;
    }
}
