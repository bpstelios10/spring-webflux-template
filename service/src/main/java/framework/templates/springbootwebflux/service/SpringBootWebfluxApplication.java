package framework.templates.springbootwebflux.service;

import framework.templates.springbootwebflux.service.clients.rest.QuoteRandomProperties;
import framework.templates.springbootwebflux.service.clients.rest.WebClientProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({WebClientProperties.class, QuoteRandomProperties.class})
public class SpringBootWebfluxApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootWebfluxApplication.class, args);
    }
}
