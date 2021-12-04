package framework.templates.springbootwebflux.service.clients.rest;

import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Counter;
import io.prometheus.client.Histogram;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;

@Component
public class DownstreamMetricService {

    private static final double NANOSECONDS_PER_SECOND = 1E9;

    private final Counter applicationDownstreamCounter = Counter.build()
            .name("downstream_responses")
            .help("Responses from requests to rest dependencies per endpoint")
            .labelNames("dependency", "endpoint", "status")
            .create();

    private final Histogram applicationDownstreamLatencyHistogram = Histogram.build()
            .name("downstream_latency_seconds")
            .help("Responses time percentiles from requests to rest dependencies per endpoint")
            .labelNames("dependency", "endpoint")
            .buckets(0.005, 0.025, 0.05, 0.1, 0.15, 0.2, 0.25, 0.3, 0.35, 0.4, 0.45, 0.5, 0.6, 0.7, 0.8, 0.9, 1, 1.5, 2, 2.5, 3, 3.5, 4, 4.5, 5)
            .create();

    @Autowired
    public DownstreamMetricService(CollectorRegistry collectorRegistry) {
        applicationDownstreamCounter.register(collectorRegistry);
        applicationDownstreamLatencyHistogram.register(collectorRegistry);
    }

    public void recordDownstreamApplicationResponseMetrics(String dependency, String endpointLabel, String statusCode, Instant startTime) {
        applicationDownstreamCounter.labels(dependency, endpointLabel, statusCode).inc();
        applicationDownstreamLatencyHistogram.labels(dependency, endpointLabel).observe(getNanos(startTime) / NANOSECONDS_PER_SECOND);
    }

    private long getNanos(Instant startTime) {
        return Duration.between(startTime, Instant.now()).toNanos();
    }
}
