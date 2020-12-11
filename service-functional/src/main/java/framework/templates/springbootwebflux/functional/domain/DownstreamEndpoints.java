package framework.templates.springbootwebflux.functional.domain;

import lombok.Getter;

@Getter
public enum DownstreamEndpoints {
    QUOTE_RANDOM("TRONALDDUMP", "/random/quote"),
    QUOTE_SEARCH("TRONALDDUMP", "/search/quote");

    private final String dependency;
    private final String path;

    DownstreamEndpoints(String dependency, String path) {
        this.dependency = dependency;
        this.path = path;
    }
}
