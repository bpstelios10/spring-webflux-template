package framework.templates.springbootwebflux.functional.metrics;

import framework.templates.springbootwebflux.functional.client.ServiceClient;
import framework.templates.springbootwebflux.functional.client.ServiceRequest;
import framework.templates.springbootwebflux.functional.client.ServiceRequestGenerator;
import framework.templates.springbootwebflux.functional.client.ServiceResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServiceMetricsProviderTest {

    @Mock
    private ServiceClient serviceClient;
    @Mock
    private ServiceRequestGenerator requestGenerator;
    @Mock
    private ServiceResponse serviceResponse;
    @Mock
    private ServiceRequest.ServiceRequestBuilder serviceResponseBuilder;
    private ServiceMetricsProvider serviceMetricsProvider;

    @BeforeEach
    void setup() {
        when(requestGenerator.serviceRequestBuilder("/private/metrics")).thenReturn(serviceResponseBuilder);
        serviceMetricsProvider = new ServiceMetricsProvider(serviceClient, requestGenerator);
    }

    @ParameterizedTest(name = "{index}: scrapedMetrics: [{0}], testMetric {1}, expectedMetricValue: [{2}]")
    @MethodSource("scenarios")
    void getCurrentValue_doesNotThrowException_whenScrapingMetricsSucceeds(
            String scrapedMetrics, String testMetric, BigDecimal expectedMetricValue) {
        doNothing().when(serviceClient).execute(any());
        when(serviceResponse.getStatusCode()).thenReturn(200);
        when(serviceResponse.getBody()).thenReturn(scrapedMetrics);
        when(serviceClient.getResponse()).thenReturn(serviceResponse);

        BigDecimal testMetricValue = serviceMetricsProvider.getCurrentMetricValue(testMetric);

        assertThat(testMetricValue).isEqualTo(expectedMetricValue);
    }

    @Test
    void getCurrentValue_throwsException_whenUnableToScrapeMetrics() {
        doNothing().when(serviceClient).execute(any());
        when(serviceResponse.getStatusCode()).thenReturn(500);
        when(serviceClient.getResponse()).thenReturn(serviceResponse);

        AssertionError assertionError = assertThrows(AssertionError.class, () -> serviceMetricsProvider.getCurrentMetricValue(""));
        assertThat(assertionError.getMessage()).isEqualTo("Unable to obtain metrics. Endpoint returned 500");
    }

    private static Stream<Arguments> scenarios() {
        return Stream.of(
                Arguments.of("test_metric 2.0", "test_metric", BigDecimal.valueOf(2.0)),
                Arguments.of("test_metric_1 1.0\ntest_metric 2.0\ntest_metric_2 3.0", "test_metric", BigDecimal.valueOf(2.0)),
                Arguments.of("test_metric -2.0", "test_metric", BigDecimal.valueOf(-2.0)),
                Arguments.of("test_metric 2", "test_metric", BigDecimal.valueOf(0)),
                Arguments.of("test_metric_1 2.0", "test_metric", BigDecimal.valueOf(0)),
                Arguments.of("first_test_metric 2.0", "test_metric", BigDecimal.valueOf(0))
        );
    }
}