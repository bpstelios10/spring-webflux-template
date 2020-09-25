package framework.templates.springbootwebflux.service.clients.rest;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "service.quote-random")
public class QuoteRandomProperties extends DownstreamProperties {
}
