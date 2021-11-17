package framework.templates.springbootwebflux.service.clients.rest;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.prometheus.publisher.CircuitBreakerMetricsPublisher;
import io.prometheus.client.CollectorRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

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
                .slidingWindowSize(4) //in order to pass fts. should configure for fts and not
                .waitDurationInOpenState(Duration.ofMillis(500)) //in order to pass fts. should configure for fts and not
                .build();

        return circuitBreakerRegistry.circuitBreaker("QuotesService", circuitBreakerConfig);
    }

    @Bean
    CircuitBreaker yodaSpeechServiceCircuitBreaker(CircuitBreakerRegistry circuitBreakerRegistry) {
        CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig
                .custom()
                .minimumNumberOfCalls(4)
                .slidingWindowSize(4) //in order to pass fts. should configure for fts and not
                .waitDurationInOpenState(Duration.ofMillis(500)) //in order to pass fts. should configure for fts and not
                .build();

        return circuitBreakerRegistry.circuitBreaker("YodaSpeechService", circuitBreakerConfig);
    }
}
