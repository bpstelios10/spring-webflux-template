package framework.templates.springbootwebflux.functional.steps;

import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.http.HttpHeader;
import com.github.tomakehurst.wiremock.http.HttpHeaders;
import cucumber.api.java8.En;

import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static framework.templates.springbootwebflux.functional.domain.Constants.EMPTY;
import static framework.templates.springbootwebflux.functional.domain.Constants.NOT_PRESENT;

public class DownstreamYodaSpeechStepDefinitions implements En {
    private static final String YODA_TRANSLATE_URL = "/translate/yoda";
    private static final UUID YODA_TRANSLATE_MAPPING_UUID = UUID.fromString("959a8290-6712-4697-afb9-8102f056e91a");

    public DownstreamYodaSpeechStepDefinitions() {
        Given("^yoda-speech is primed to return a successful response with (.*) header set to (.*)$",
                (String headerName, String headerValue) -> {
                    HttpHeaders headers = HttpHeaders.copyOf(
                            getSingleStubMapping(YODA_TRANSLATE_MAPPING_UUID).getResponse().getHeaders());
                    switch (headerValue) {
                        case (NOT_PRESENT):
                            break;
                        case (EMPTY):
                            headers = headers.plus(new HttpHeader(headerName, ""));
                            break;
                        default:
                            headers = headers.plus(new HttpHeader(headerName, headerValue));
                            break;
                    }

                    stubFor(post(urlEqualTo(YODA_TRANSLATE_URL))
                            .willReturn(aResponse()
                                    .withBody(getSingleStubMapping(YODA_TRANSLATE_MAPPING_UUID).getResponse().getBody())
                                    .withStatus(200)
                                    .withHeaders(headers)
                            ));
                }
        );
        Given("^yoda-speech is primed to return a successful response with invalid body$", () -> {
                    String baseBody = getSingleStubMapping(YODA_TRANSLATE_MAPPING_UUID).getResponse().getBody().replaceAll(":", " ");

                    stubFor(post(urlEqualTo(YODA_TRANSLATE_URL))
                            .willReturn(aResponse().withBody(baseBody)
                                    .withHeaders(getSingleStubMapping(YODA_TRANSLATE_MAPPING_UUID).getResponse().getHeaders())
                                    .withStatus(200)));
                }
        );
        Given("^yoda-speech is primed to return a (\\d+) response with body (.*)$",
                (Integer statusCode, String errorMessage) -> {
                    ResponseDefinitionBuilder errorResponse = aResponse()
                            .withStatus(statusCode)
                            .withBody("{" +
                                    "    \"message\":\"" + errorMessage + "\"," +
                                    "}")
                            .withHeader("Content-Type", "application/json");

                    stubFor(post(YODA_TRANSLATE_URL).willReturn(errorResponse));
                }
        );
        Given("^yoda-speech is primed to return a successful response with fixed delay of (\\d+) milliseconds$",
                (Integer responseDelayMillis) ->
                        stubFor(post(YODA_TRANSLATE_URL).willReturn(
                                aResponse()
                                        .withBody(getSingleStubMapping(YODA_TRANSLATE_MAPPING_UUID).getResponse().getBody())
                                        .withHeaders(getSingleStubMapping(YODA_TRANSLATE_MAPPING_UUID).getResponse().getHeaders())
                                        .withStatus(200)
                                        .withFixedDelay(responseDelayMillis)
                        ))
        );
        Given("^yoda-speech is primed to return a successful response with dribbled delay of (\\d+) milliseconds in (\\d+) chunks$",
                (Integer responseDelayMillis, Integer chunks) ->
                        stubFor(post(YODA_TRANSLATE_URL).willReturn(
                                aResponse()
                                        .withBody(getSingleStubMapping(YODA_TRANSLATE_MAPPING_UUID).getResponse().getBody())
                                        .withHeaders(getSingleStubMapping(YODA_TRANSLATE_MAPPING_UUID).getResponse().getHeaders())
                                        .withStatus(200)
                                        .withChunkedDribbleDelay(chunks, responseDelayMillis)
                        ))
        );
    }
}
