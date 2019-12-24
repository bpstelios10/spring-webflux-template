package framework.templates.springbootwebflux.service.web.filter;

import framework.templates.springbootwebflux.service.service.MetricService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.http.server.reactive.MockServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.OK;

@RunWith(MockitoJUnitRunner.class)
public class MetricsFilterTest {

    @InjectMocks
    MetricsFilter metricsFilter;
    @Mock
    MetricService metricService;
    @Mock
    ServerWebExchange exchange;
    @Mock
    WebFilterChain chain;

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