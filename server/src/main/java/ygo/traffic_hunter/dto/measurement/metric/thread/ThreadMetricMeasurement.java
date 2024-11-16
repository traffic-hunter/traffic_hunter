package ygo.traffic_hunter.dto.measurement.metric.thread;

public record ThreadMetricMeasurement(
        int threadCount,
        int getPeekThreadCount,
        long getTotalStartThreadCount
) {
}
