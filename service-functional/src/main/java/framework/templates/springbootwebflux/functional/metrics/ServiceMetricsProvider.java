package framework.templates.springbootwebflux.functional.metrics;

import framework.templates.springbootwebflux.functional.client.ServiceClient;
import framework.templates.springbootwebflux.functional.client.ServiceRequest;
import framework.templates.springbootwebflux.functional.client.ServiceRequestGenerator;
import framework.templates.springbootwebflux.functional.client.ServiceResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.String.format;
import static java.math.BigDecimal.ZERO;
import static org.assertj.core.api.Java6Assertions.assertThat;

@Component
@Scope("cucumber-glue")
public class ServiceMetricsProvider {

    private final ServiceClient serviceClient;
    private final ServiceRequest.ServiceRequestBuilder serviceRequestBuilder;

    @Autowired
    public ServiceMetricsProvider(ServiceClient serviceClient, ServiceRequestGenerator serviceRequestGenerator) {
        this.serviceClient = serviceClient;
        this.serviceRequestBuilder = serviceRequestGenerator.serviceRequestBuilder("/private/metrics");
    }

    public BigDecimal getCurrentMetricValue(String metricKey) {
        Matcher matcher = matchMetric(metricKey);

        return matcher.find() ? new BigDecimal(matcher.group(2)) : ZERO;
    }

    private Matcher matchMetric(String metric) {
        String metricPattern = "\\b(%s)\\s(-?[0-9]+\\.[0-9]+)";

        return Pattern.compile(format(metricPattern, metric)).matcher(getMetrics());
    }

    private String getMetrics() {
        serviceClient.execute(serviceRequestBuilder.build());
        ServiceResponse response = serviceClient.getResponse();

        assertThat(response.getStatusCode())
                .overridingErrorMessage("Unable to obtain metrics. Endpoint returned %s", response.getStatusCode())
                .isEqualTo(200);

        return response.getBody();
    }
}
