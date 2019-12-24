package framework.templates.springbootwebflux.service.service;

import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Counter;
import io.prometheus.client.Histogram;
import io.prometheus.client.hotspot.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MetricService {

    private static final double NANOSECONDS_PER_SECOND = 1E9;

    private final Counter responseCounter = Counter.build()
            .name("application_responses")
            .help("Metrics for application responses per endpoint, method and http response code.")
            .labelNames("endpointName", "status")
            .create();

    private final Histogram applicationLatencyHistogram = Histogram.build()
            .name("application_latency")
            .help("Application latencies")
            .labelNames("endpointName")
            .buckets(0.005, 0.025, 0.05, 0.1, 0.15, 0.2, 0.25, 0.3, 0.35, 0.4, 0.45, 0.5, 0.6, 0.7, 0.8, 0.9, 1, 1.5, 2, 2.5, 3, 3.5, 4, 4.5, 5)
            .create();

    @Autowired
    public MetricService(CollectorRegistry collectorRegistry) {
        responseCounter.register(collectorRegistry);
        applicationLatencyHistogram.register(collectorRegistry);

        registerJvmMetrics(collectorRegistry);
    }

    public void recordApplicationResponseMetrics(String endpointLabel, String statusCode, long nanos) {
        responseCounter.labels(endpointLabel, statusCode).inc();
        applicationLatencyHistogram.labels(endpointLabel).observe(nanos / NANOSECONDS_PER_SECOND);
    }

    private void registerJvmMetrics(CollectorRegistry collectorRegistry) {
        new StandardExports().register(collectorRegistry);
        new MemoryPoolsExports().register(collectorRegistry);
        new MemoryAllocationExports().register(collectorRegistry);
        new BufferPoolsExports().register(collectorRegistry);
        new ThreadExports().register(collectorRegistry);
        new GarbageCollectorExports().register(collectorRegistry);
    }
}