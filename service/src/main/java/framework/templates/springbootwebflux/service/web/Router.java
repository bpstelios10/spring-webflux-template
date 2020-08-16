package framework.templates.springbootwebflux.service.web;

import framework.templates.springbootwebflux.service.error.InvalidAcceptHeaderException;
import framework.templates.springbootwebflux.service.web.handler.HealthCheckHandler;
import framework.templates.springbootwebflux.service.web.handler.RandomQuoteHandler;
import framework.templates.springbootwebflux.service.web.handler.SearchQuoteHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.util.Assert;
import org.springframework.web.reactive.function.server.RequestPredicate;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import java.util.List;
import java.util.Set;

import static framework.templates.springbootwebflux.service.web.domain.Endpoints.*;
import static framework.templates.springbootwebflux.service.web.domain.ServiceHeaders.*;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.headers;

@Configuration
public class Router {

    @Bean
    public RouterFunction<ServerResponse> route(HealthCheckHandler healthCheckHandler,
                                                RandomQuoteHandler randomQuoteHandler,
                                                SearchQuoteHandler searchQuoteHandler) {
        return RouterFunctions
                .route(GET(PRIVATE_STATUS.getPath()), healthCheckHandler)
                .andRoute(GET(QUOTE_RANDOM.getPath()).and(acceptOrEmptyAccept(SEARCH_QUOTE_ALLOWED_ACCEPT_TYPES)), randomQuoteHandler)
                .andRoute(GET(QUOTE_SEARCH.getPath()).and(acceptOrEmptyAccept(RANDOM_QUOTE_ALLOWED_ACCEPT_TYPES)), searchQuoteHandler);
    }

    private static RequestPredicate acceptOrEmptyAccept(Set<MediaType> mediaTypes) {
        Assert.notEmpty(mediaTypes, "'mediaTypes' must not be empty");

        return headers(headers -> {
            List<MediaType> requestHeaderAcceptType = headers.accept();
            if (requestHeaderAcceptType.isEmpty() ||
                    getFirstValidAcceptHeader(mediaTypes, requestHeaderAcceptType) != null) {
                return true;
            }
            throw new InvalidAcceptHeaderException();
        });
    }
}
