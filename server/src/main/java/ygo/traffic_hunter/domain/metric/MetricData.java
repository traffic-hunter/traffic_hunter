package ygo.traffic_hunter.domain.metric;

import ygo.traffic_hunter.domain.metric.cpu.CpuMetricMeasurement;
import ygo.traffic_hunter.domain.metric.memory.MemoryMetricMeasurement;
import ygo.traffic_hunter.domain.metric.thread.ThreadMetricMeasurement;
import ygo.traffic_hunter.domain.metric.gc.GCMetricMeasurement;
import ygo.traffic_hunter.domain.metric.runtime.RuntimeMetricMeasurement;

public record MetricData(
        CpuMetricMeasurement cpuMetric,

        GCMetricMeasurement gcMetric,

        MemoryMetricMeasurement memoryMetric,

        RuntimeMetricMeasurement runtimeMetric,

        ThreadMetricMeasurement threadMetric
) {
}
