package framework.templates.springbootwebflux.service.web.filter;

import com.google.common.collect.ImmutableList;
import framework.templates.springbootwebflux.service.service.MetricService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Component
public class MetricsFilter implements WebFilter {

    private static final List<String> METRIC_EXCLUDED_ENDPOINTS = ImmutableList.of("favicon");

    private final MetricService metricService;

    @Autowired
    public MetricsFilter(MetricService metricService) {
        this.metricService = metricService;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String requestPath = exchange.getRequest().getPath().pathWithinApplication().value();

        if (METRIC_EXCLUDED_ENDPOINTS.stream().anyMatch(requestPath::contains)) {
            return chain.filter(exchange);
        }

        Instant startTime = Instant.now();
        return chain.filter(exchange)
                .doFinally(signalType -> metricService.recordApplicationResponseMetrics(requestPath,
                        String.valueOf(exchange.getResponse().getStatusCode().value()), getNanos(startTime)));
    }

    private long getNanos(Instant startTime) {
        return Duration.between(startTime, Instant.now()).toNanos();
    }
}