package framework.templates.springbootwebflux.functional.steps;

import com.github.tomakehurst.wiremock.client.WireMock;
import io.cucumber.java8.En;
import org.springframework.beans.factory.annotation.Value;

public class Hooks implements En {

    public Hooks(@Value("${wiremock.host}") String wiremockHost,
                 @Value("${wiremock.port}") int wiremockPort,
                 @Value("${wiremock.context-path}") String wiremockContextPath) {
        Before(() -> {
            Runtime.getRuntime().addShutdownHook(new Thread(this::doAfterAllTestsFinish));
            doBeforeAnyTestStart(wiremockHost, wiremockPort, wiremockContextPath);
        });
    }

    private void doBeforeAnyTestStart(String wiremockHost, int wiremockPort, String wiremockContextPath) {
        WireMock.configureFor(wiremockHost, wiremockPort, wiremockContextPath);
        WireMock.reset();
    }

    private void doAfterAllTestsFinish() {
    }
}
