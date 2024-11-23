package ygo.traffic_hunter.domain.metric.memory;

import ygo.traffic_hunter.domain.metric.memory.usage.MemoryMetricUsage;

public record MemoryMetricMeasurement(

        MemoryMetricUsage heapMemoryUsage,

        MemoryMetricUsage nonHeapMemoryUsage
) {
}
