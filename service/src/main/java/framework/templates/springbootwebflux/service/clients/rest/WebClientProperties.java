package framework.templates.springbootwebflux.service.clients.rest;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Data
@Builder
@Validated
@AllArgsConstructor
@NoArgsConstructor
@ConfigurationProperties(prefix = "service.webclient")
public class WebClientProperties {
    @NotNull
    @Positive
    private Integer poolSize;
    @NotNull
    @Positive
    private Integer connectionRequestTimeoutMs;
    @NotNull
    @Positive
    private Integer connectTimeoutMs;
    @NotNull
    @Positive
    private Integer readTimeoutMs;
    private Integer totalRequestTimeout;
}
