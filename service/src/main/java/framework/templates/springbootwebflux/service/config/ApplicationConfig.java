package framework.templates.springbootwebflux.service.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.prometheus.client.CollectorRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {

    @Bean
    CollectorRegistry collectorRegistry() {
        return new CollectorRegistry();
    }

    @Bean
    ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
