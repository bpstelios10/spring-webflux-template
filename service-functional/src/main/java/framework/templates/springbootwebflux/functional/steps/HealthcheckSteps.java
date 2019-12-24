package framework.templates.springbootwebflux.functional.steps;

import cucumber.api.java8.En;
import framework.templates.springbootwebflux.functional.client.ServiceClient;
import framework.templates.springbootwebflux.functional.client.ServiceRequestGenerator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpStatus.OK;

public class HealthcheckSteps implements En {

    public HealthcheckSteps(ServiceClient client, ServiceRequestGenerator requestGenerator) {
        When("^the client intends to call healthcheck (.*) endpoint$", (String endpoint) ->
                client.execute(requestGenerator.withMethod(GET).withPath("/private/" + endpoint).generate())
        );

        Then("^a success response is returned$", () ->
                assertThat(client.getResponse().getStatusCode()).isEqualTo(OK.value())
        );

        Then("^the response body is \"([^\"]*)\"$", (String expectedResponseBody) ->
                assertThat(client.getResponse().getBody()).isEqualTo(expectedResponseBody)
        );
    }
}
