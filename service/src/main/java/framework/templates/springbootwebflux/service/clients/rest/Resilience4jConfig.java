package framework.templates.springbootwebflux.service.clients.rest;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.prometheus.publisher.CircuitBreakerMetricsPublisher;
import io.prometheus.client.CollectorRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Resilience4jConfig {

    @Bean
    CircuitBreakerRegistry circuitBreakerRegistry(CollectorRegistry collectorRegistry) {
        CircuitBreakerMetricsPublisher circuitBreakerMetricsPublisher = new CircuitBreakerMetricsPublisher();
        circuitBreakerMetricsPublisher.register(collectorRegistry);

        return CircuitBreakerRegistry.of(CircuitBreakerConfig.ofDefaults(), circuitBreakerMetricsPublisher);
    }

    @Bean
    CircuitBreaker quoteServiceCircuitBreaker(CircuitBreakerRegistry circuitBreakerRegistry) {
        CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig
                .custom()
                .minimumNumberOfCalls(4)
                .build();

        return circuitBreakerRegistry.circuitBreaker("QuotesService", circuitBreakerConfig);
    }
}
