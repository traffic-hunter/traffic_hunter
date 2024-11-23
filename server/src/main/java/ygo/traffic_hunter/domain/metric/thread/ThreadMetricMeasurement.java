package ygo.traffic_hunter.domain.metric.thread;

public record ThreadMetricMeasurement(

        int threadCount,

        int getPeekThreadCount,

        long getTotalStartThreadCount
) {
}
