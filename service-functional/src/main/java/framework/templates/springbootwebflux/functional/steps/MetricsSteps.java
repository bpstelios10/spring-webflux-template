package framework.templates.springbootwebflux.functional.steps;

import cucumber.api.java8.En;
import framework.templates.springbootwebflux.functional.metrics.ServiceMetricsProvider;

import java.math.BigDecimal;
import java.util.HashMap;

import static framework.templates.springbootwebflux.service.web.domain.Endpoints.PRIVATE_STATUS;
import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;

public class MetricsSteps implements En {

    private static final String APPLICATION_RESPONSES_METRIC_TEMPLATE = "application_responses\\{endpointName=\"%s\",status=\"%s\",}";

    private HashMap<String, BigDecimal> initialMetrics = new HashMap<>();

    public MetricsSteps(ServiceMetricsProvider serviceMetricsProvider) {
        Given("^application response metric for healthcheck status with response status (.*) gets initialised$", (String status) -> {
            String metricKey = format(APPLICATION_RESPONSES_METRIC_TEMPLATE, PRIVATE_STATUS.getPath(), status);
            initialMetrics.put(metricKey, serviceMetricsProvider.getCurrentMetricValue(metricKey));
        });

        Then("^application response metric for healthcheck status with response status (.*) is increased$", (String status) -> {
            String metricKey = format(APPLICATION_RESPONSES_METRIC_TEMPLATE, PRIVATE_STATUS.getPath(), status);
            if (initialMetrics.get(metricKey) == null) throw new Exception("metric is not initialised");

            assertThat(serviceMetricsProvider.getCurrentMetricValue(metricKey).subtract(initialMetrics.get(metricKey)))
                    .isEqualTo(BigDecimal.valueOf(1.0));
        });
    }
}
