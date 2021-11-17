package framework.templates.springbootwebflux.service.clients.rest.quotes;

import framework.templates.springbootwebflux.service.clients.rest.DownstreamProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Setter
@Getter
@ConfigurationProperties(prefix = "service.quote-random")
public class QuoteRandomProperties extends DownstreamProperties {
}
