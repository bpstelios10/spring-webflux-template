package framework.templates.springbootwebflux.service.clients.rest;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
public class DownstreamProperties {
    @NotNull
    private String scheme;
    @NotNull
    private String host;
    @NotNull
    @Positive
    private Integer port;
    @NotBlank
    private String path;
}
