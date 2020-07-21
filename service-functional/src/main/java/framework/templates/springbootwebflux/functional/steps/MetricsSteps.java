package framework.templates.springbootwebflux.functional.steps;

import cucumber.api.java8.En;
import framework.templates.springbootwebflux.functional.metrics.ServiceMetricsProvider;
import framework.templates.springbootwebflux.service.web.domain.Endpoints;

import java.math.BigDecimal;
import java.util.HashMap;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;

public class MetricsSteps implements En {

    private static final String APPLICATION_RESPONSES_METRIC_TEMPLATE = "application_responses\\{endpointName=\"%s\",status=\"%s\",}";

    private HashMap<String, BigDecimal> initialMetrics = new HashMap<>();

    public MetricsSteps(ServiceMetricsProvider serviceMetricsProvider) {
        Given("^application response metric for (.*) with response status (.*) gets initialised$", (String endpoint, String status) -> {
            String metricKey = format(APPLICATION_RESPONSES_METRIC_TEMPLATE, Endpoints.valueOf(endpoint).getPath(), status);
            initialMetrics.put(metricKey, serviceMetricsProvider.getCurrentMetricValue(metricKey));
        });

        Then("^application response metric for (.*) with response status (.*) is increased$", (String endpoint, String status) -> {
            String metricKey = format(APPLICATION_RESPONSES_METRIC_TEMPLATE, Endpoints.valueOf(endpoint).getPath(), status);
            if (initialMetrics.get(metricKey) == null) throw new Exception("metric is not initialised");

            assertThat(serviceMetricsProvider.getCurrentMetricValue(metricKey).subtract(initialMetrics.get(metricKey)))
                    .isEqualTo(BigDecimal.valueOf(1.0));
        });
    }
}
