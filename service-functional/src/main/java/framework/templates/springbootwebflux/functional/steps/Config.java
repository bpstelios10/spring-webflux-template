package framework.templates.springbootwebflux.functional.steps;

import framework.templates.springbootwebflux.functional.client.ServiceClient;
import framework.templates.springbootwebflux.functional.client.ServiceRequestGenerator;
import framework.templates.springbootwebflux.functional.config.AppConfiguration;
import framework.templates.springbootwebflux.functional.config.LocalStartupManager;
import framework.templates.springbootwebflux.functional.metrics.ServiceMetricsProvider;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.test.context.ContextConfiguration;

@CucumberContextConfiguration
@ContextConfiguration(classes = {AppConfiguration.class, LocalStartupManager.class, ServiceMetricsProvider.class,
        ServiceRequestGenerator.class, ServiceClient.class})
public class Config {
}
