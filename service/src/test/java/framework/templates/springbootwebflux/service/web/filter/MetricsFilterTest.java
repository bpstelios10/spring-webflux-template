package framework.templates.springbootwebflux.service.web.filter;

import framework.templates.springbootwebflux.service.service.MetricService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.http.server.reactive.MockServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.OK;

@ExtendWith(MockitoExtension.class)
public class MetricsFilterTest {

    @Mock
    private MetricService metricService;
    @Mock
    private ServerWebExchange exchange;
    @Mock
    private WebFilterChain chain;
    private MetricsFilter metricsFilter;

    @BeforeEach
    void setup() {
        metricsFilter = new MetricsFilter(metricService);
    }

    @Test
    public void filter_succeeds_forNotExcludedPaths() {
        MockServerHttpResponse mockServerHttpResponse = new MockServerHttpResponse();
        mockServerHttpResponse.setStatusCode(OK);

        when(exchange.getRequest()).thenReturn(MockServerHttpRequest.get("path").build());
        when(exchange.getResponse()).thenReturn(mockServerHttpResponse);
        doNothing().when(metricService).recordApplicationResponseMetrics(eq("path"), eq("200"), anyLong());
        when(chain.filter(exchange)).thenReturn(Mono.just("kati").then());

        StepVerifier.create(metricsFilter.filter(exchange, chain))
                .verifyComplete();
    }

    @Test
    public void filter_succeeds_forExcludedPaths() {
        MockServerHttpResponse mockServerHttpResponse = new MockServerHttpResponse();
        mockServerHttpResponse.setStatusCode(OK);

        when(exchange.getRequest()).thenReturn(MockServerHttpRequest.get("favicon").build());
        when(chain.filter(exchange)).thenReturn(Mono.empty());

        StepVerifier.create(metricsFilter.filter(exchange, chain))
                .verifyComplete();

        verifyNoInteractions(metricService);
    }
}
