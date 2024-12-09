package ygo.traffic_hunter.domain.metric.thread;

/**
 * @author yungwang-o
 * @version 1.0.0
 */
public record ThreadMetricMeasurement(

        int threadCount,

        int getPeekThreadCount,

        long getTotalStartThreadCount
) {
}
