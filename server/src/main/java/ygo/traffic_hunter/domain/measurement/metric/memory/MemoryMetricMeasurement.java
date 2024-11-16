package ygo.traffic_hunter.domain.measurement.metric.memory;

import org.influxdb.annotation.Column;
import ygo.traffic_hunter.domain.measurement.metric.MetricMeasurement;
import ygo.traffic_hunter.domain.measurement.metric.memory.usage.MemoryMetricUsage;

public record MemoryMetricMeasurement(

        @Column(name = "heap_memory_usage")
        MemoryMetricUsage heapMemoryUsage,

        @Column(name = "non_heap_memory_usage")
        MemoryMetricUsage nonHeapMemoryUsage
) {
}
