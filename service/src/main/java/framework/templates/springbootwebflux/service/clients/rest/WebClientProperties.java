package framework.templates.springbootwebflux.service.clients.rest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
@Builder
@Validated
@AllArgsConstructor
@NoArgsConstructor
@ConfigurationProperties(prefix = "service.webclient")
public class WebClientProperties {
    @NotNull
    @Positive
    private Integer connectTimeoutMs;
    @NotNull
    @Positive
    private Integer connectionRequestTimeoutMs;
    @NotNull
    @Positive
    private Integer readTimeoutMs;
    @NotNull
    @Positive
    private Integer poolSize;
    private Integer totalRequestTimeout;
}
