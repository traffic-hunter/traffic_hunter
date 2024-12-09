package ygo.traffic_hunter.domain.metric.memory;

import ygo.traffic_hunter.domain.metric.memory.usage.MemoryMetricUsage;

/**
 * @author yungwang-o
 * @version 1.0.0
 */
public record MemoryMetricMeasurement(

        MemoryMetricUsage heapMemoryUsage,

        MemoryMetricUsage nonHeapMemoryUsage
) {
}
