package framework.templates.springbootwebflux.functional.steps;

import cucumber.api.java8.En;
import framework.templates.springbootwebflux.functional.client.ServiceClient;
import framework.templates.springbootwebflux.functional.client.ServiceRequestGenerator;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpStatus.OK;

public class HealthcheckSteps implements En {

    private final ServiceClient client;
    private final ServiceRequestGenerator requestGenerator;

    @Autowired
    public HealthcheckSteps(ServiceClient client, ServiceRequestGenerator requestGenerator) {
        this.client = client;
        this.requestGenerator = requestGenerator;

        When("^the client intends to call healthcheck (.*) endpoint$", (String endpoint) ->
            client.execute(requestGenerator.withMethod(GET).withPath("/" + endpoint).generate())
        );

        Then("^a success response is returned$", () ->
            assertThat(client.getResponse().getStatusCode()).isEqualTo(OK.value())
        );

        Then("^the response body is \"([^\"]*)\"$", (String expectedResponseBody) ->
            assertThat(client.getResponse().getBody()).isEqualTo(expectedResponseBody)
        );
    }

}
