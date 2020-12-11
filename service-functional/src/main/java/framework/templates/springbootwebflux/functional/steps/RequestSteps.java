package framework.templates.springbootwebflux.functional.steps;

import cucumber.api.java8.En;
import framework.templates.springbootwebflux.functional.client.ServiceClient;
import framework.templates.springbootwebflux.functional.client.ServiceRequest;
import framework.templates.springbootwebflux.functional.client.ServiceRequestGenerator;
import framework.templates.springbootwebflux.functional.domain.ServiceEndpoints;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

public class RequestSteps implements En {

    public RequestSteps(ServiceClient client, ServiceRequestGenerator requestGenerator) {
        ServiceRequest.ServiceRequestBuilder requestBuilder = requestGenerator.serviceRequestBuilder("CHANGE ME !!");

        Given("^the client intends to call (.*) endpoint$", (String endpoint) ->
                requestBuilder.path(ServiceEndpoints.valueOf(endpoint).getPath())
        );
        Given("^request has header with name (.*) and value (.*)$", (String headerName, String headerValue) ->
                requestBuilder.headers(Collections.singletonMap(headerName, headerValue))
        );
        When("^the client makes the call$", () ->
                client.execute(requestBuilder.build())
        );
        Then("^a response with code (\\d+) is returned$", (Integer responseStatus) ->
                assertThat(client.getResponse().getStatusCode()).isEqualTo(responseStatus)
        );
        Then("^the response body is (.*)$", (String expectedResponseBody) -> {
                    if ("empty".equals(expectedResponseBody)) {
                        assertThat(client.getResponse().getBody()).isEmpty();
                    } else {
                        assertThat(client.getResponse().getBody()).isEqualTo(expectedResponseBody);
                    }
                }
        );
        Then("^the response contains header with name (.*) and value (.*)$", (String headerName, String headerValue) ->
                assertThat(client.getResponse().getHeaders().entrySet().stream().anyMatch(
                        entry -> entry.getKey().equals(headerName) && entry.getValue().equals(headerValue))
                ).isTrue()
        );
    }
}
