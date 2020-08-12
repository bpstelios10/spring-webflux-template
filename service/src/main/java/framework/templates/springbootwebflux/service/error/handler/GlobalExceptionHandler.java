package framework.templates.springbootwebflux.service.error.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import framework.templates.springbootwebflux.service.error.HttpError;
import framework.templates.springbootwebflux.service.error.ServiceError;
import framework.templates.springbootwebflux.service.error.WebApplicationException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Slf4j
@Order(-2)
@Component
public class GlobalExceptionHandler implements WebExceptionHandler {
    private final ServiceError defaultError;
    private final ObjectMapper objectMapper;

    @Autowired
    public GlobalExceptionHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.defaultError = ServiceError.of(INTERNAL_SERVER_ERROR, "Unexpected exception");
    }

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable throwable) {
        HttpError responseError;
        if (throwable instanceof WebApplicationException) {
            responseError = HttpError.getHttpError((WebApplicationException) throwable);
        } else {
            responseError = HttpError.getHttpError(new WebApplicationException(defaultError));
        }

        return sendResponse(exchange, responseError)
                .doFinally((t) ->
                        log.error("Returning Status Code: {}, Response Body: {}, Request Path: {} Caused by: ",
                                responseError.getStatusCode(),
                                new String(toJson(responseError)),
                                exchange.getRequest().getPath(),
                                throwable)
                );
    }

    private Mono<Void> sendResponse(ServerWebExchange exchange, HttpError responseError) {
        if (exchange.getResponse().getHeaders().get(CONTENT_TYPE) != null) {
            exchange.getResponse().getHeaders().set(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        } else {
            exchange.getResponse().getHeaders().add(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        }
        return sendResponse(exchange, responseError.getStatusCode(), responseError.getHeaders(), toJson(responseError));
    }

    private Mono<Void> sendResponse(ServerWebExchange exchange, HttpStatus httpStatus, MultiValueMap<String, String> headers, byte[] body) {
        exchange.getResponse().setStatusCode(httpStatus);
        exchange.getResponse().getHeaders().addAll(headers);
        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(body);
        return exchange.getResponse().writeWith(Flux.just(buffer));
    }

    @SneakyThrows
    private byte[] toJson(HttpError responseError) {
        return isBlank(responseError.getErrorDescription()) ? new byte[]{} : objectMapper.writeValueAsBytes(responseError);
    }
}
