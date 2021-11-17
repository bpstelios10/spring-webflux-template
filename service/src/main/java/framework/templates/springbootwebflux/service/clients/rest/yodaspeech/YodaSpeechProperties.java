package framework.templates.springbootwebflux.service.clients.rest.yodaspeech;

import framework.templates.springbootwebflux.service.clients.rest.DownstreamProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Setter
@Getter
@ConfigurationProperties(prefix = "service.yoda-speech")
public class YodaSpeechProperties extends DownstreamProperties {
}
