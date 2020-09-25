package framework.templates.springbootwebflux.service.clients.rest;

import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

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
//    @NotBlank
//    private String readinessPath;
}
