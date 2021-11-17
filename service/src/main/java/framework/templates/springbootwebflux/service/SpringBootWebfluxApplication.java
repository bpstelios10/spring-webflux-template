package framework.templates.springbootwebflux.service;

import framework.templates.springbootwebflux.service.clients.rest.WebClientProperties;
import framework.templates.springbootwebflux.service.clients.rest.quotes.QuoteRandomProperties;
import framework.templates.springbootwebflux.service.clients.rest.yodaspeech.YodaSpeechProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({WebClientProperties.class, QuoteRandomProperties.class, YodaSpeechProperties.class})
public class SpringBootWebfluxApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootWebfluxApplication.class, args);
    }
}
