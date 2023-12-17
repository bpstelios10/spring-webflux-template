package framework.templates.springbootwebflux.functional.steps;

import framework.templates.springbootwebflux.functional.domain.DownstreamEndpoints;
import framework.templates.springbootwebflux.functional.domain.ServiceEndpoints;
import framework.templates.springbootwebflux.functional.metrics.ServiceMetricsProvider;
import io.cucumber.java8.En;

import java.math.BigDecimal;
import java.util.HashMap;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;

public class MetricsSteps implements En {

    private static final String APPLICATION_RESPONSES_METRIC_TEMPLATE = "application_responses_total\\{endpointName=\"%s\",status=\"%s\",}";
    private static final String DOWNSTREAM_RESPONSES_METRIC_TEMPLATE = "downstream_responses_total\\{dependency=\"%s\",endpoint=\"%s\",status=\"%s\",}";

    private final HashMap<String, BigDecimal> initialMetrics = new HashMap<>();

    public MetricsSteps(ServiceMetricsProvider serviceMetricsProvider) {
        Given("^application response metric for (.*) with response status (.*) gets initialised$", (String endpoint, String status) -> {
            String metricKey = format(APPLICATION_RESPONSES_METRIC_TEMPLATE, ServiceEndpoints.valueOf(endpoint).getPath(), status);
            initialMetrics.put(metricKey, serviceMetricsProvider.getCurrentMetricValue(metricKey));
        });

        Then("^application response metric for (.*) with response status (.*) is increased$", (String endpoint, String status) -> {
            String metricKey = format(APPLICATION_RESPONSES_METRIC_TEMPLATE, ServiceEndpoints.valueOf(endpoint).getPath(), status);
            if (initialMetrics.get(metricKey) == null) throw new Exception("metric is not initialised");

            assertThat(serviceMetricsProvider.getCurrentMetricValue(metricKey).subtract(initialMetrics.get(metricKey)))
                    .isEqualTo(BigDecimal.valueOf(1.0));
        });
        Given("^downstream response metric for (.*) with response status (.*) gets initialised$", (String endpoint, String status) -> {
            String metricKey = format(DOWNSTREAM_RESPONSES_METRIC_TEMPLATE,
                    DownstreamEndpoints.valueOf(endpoint).getDependency(), DownstreamEndpoints.valueOf(endpoint).getPath(), status);
            initialMetrics.put(metricKey, serviceMetricsProvider.getCurrentMetricValue(metricKey));
        });

        Then("^downstream response metric for (.*) with response status (.*) is increased$", (String endpoint, String status) -> {
            String metricKey = format(DOWNSTREAM_RESPONSES_METRIC_TEMPLATE,
                    DownstreamEndpoints.valueOf(endpoint).getDependency(), DownstreamEndpoints.valueOf(endpoint).getPath(), status);
            if (initialMetrics.get(metricKey) == null) throw new Exception("metric is not initialised");

            assertThat(serviceMetricsProvider.getCurrentMetricValue(metricKey).subtract(initialMetrics.get(metricKey)))
                    .isEqualTo(BigDecimal.valueOf(1.0));
        });

        Then("^downstream response metric for (.*) with response status (.*) is not increased$", (String endpoint, String status) -> {
            String metricKey = format(DOWNSTREAM_RESPONSES_METRIC_TEMPLATE,
                    DownstreamEndpoints.valueOf(endpoint).getDependency(), DownstreamEndpoints.valueOf(endpoint).getPath(), status);
            if (initialMetrics.get(metricKey) == null) throw new Exception("metric is not initialised");

            assertThat(serviceMetricsProvider
                    .getCurrentMetricValue(metricKey)
                    .subtract(initialMetrics.get(metricKey))
                    .compareTo(BigDecimal.ZERO)
            ).isEqualTo(0);
        });
    }
}
