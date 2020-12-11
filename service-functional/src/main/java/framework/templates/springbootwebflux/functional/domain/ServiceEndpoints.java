package framework.templates.springbootwebflux.functional.domain;

import lombok.Getter;

@Getter
public enum ServiceEndpoints {
    PRIVATE_STATUS("/private/status"),
    QUOTE_RANDOM("/trolltrump/quote/random"),
    QUOTE_SEARCH("/trolltrump/quote/search"),
    MISSING_ENDPOINT("/nothing-to-see-here");

    private final String path;

    ServiceEndpoints(String path) {
        this.path = path;
    }
}
