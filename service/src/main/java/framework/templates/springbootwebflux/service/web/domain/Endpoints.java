package framework.templates.springbootwebflux.service.web.domain;

import lombok.Getter;

@Getter
public enum Endpoints {
    PRIVATE_STATUS("/private/status");

    private final String path;

    Endpoints(String path) {
        this.path = path;
    }
}
