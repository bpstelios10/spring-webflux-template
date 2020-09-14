package framework.templates.springbootwebflux.service.web.domain;

import lombok.Getter;

@Getter
public enum Endpoints {
    PRIVATE_STATUS("/private/status"),
    QUOTE_RANDOM("/trolltrump/quote/random"),
    QUOTE_SEARCH("/trolltrump/quote/search"),
    MISSING_ENDPOINT("/nothing-to-see-here");

    private final String path;

    Endpoints(String path) {
        this.path = path;
    }
}
