package framework.templates.springbootwebflux.service.web.domain;

import lombok.Data;

@Data(staticConstructor = "of")
public class QuoteResponse {
    //TODO consider switching to specific http response code, when no content found
    public static QuoteResponse EMPTY_QUOTE_RESPONSE = QuoteResponse.of("No quotes were found");

    private final String quote;
}
