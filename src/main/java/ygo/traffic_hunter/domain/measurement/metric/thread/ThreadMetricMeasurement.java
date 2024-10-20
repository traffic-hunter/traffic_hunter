package ygo.traffic_hunter.domain.measurement.metric.thread;

public record ThreadMetricMeasurement(
        int threadCount,
        int getPeekThreadCount,
        long getTotalStartThreadCount
) {
}
