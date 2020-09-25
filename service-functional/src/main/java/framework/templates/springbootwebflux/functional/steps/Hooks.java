package framework.templates.springbootwebflux.functional.steps;

import com.github.tomakehurst.wiremock.client.WireMock;
import cucumber.api.java.Before;
import org.springframework.beans.factory.annotation.Value;

public class Hooks {

    @Value("${wiremock.host}")
    private String wiremockHost;

    @Value("${wiremock.port}")
    private int wiremockPort;

    @Value("${wiremock.context-path}")
    private String wiremockContextPath;

    @Before
    public void initialiseScenarioState() {
        WireMock.configureFor(wiremockHost, wiremockPort, wiremockContextPath);
        WireMock.reset();
    }
}
