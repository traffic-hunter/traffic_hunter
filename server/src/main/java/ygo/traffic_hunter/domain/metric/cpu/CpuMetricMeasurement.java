package ygo.traffic_hunter.domain.metric.cpu;

/**
 * @author yungwang-o
 * @version 1.0.0
 */
public record CpuMetricMeasurement(
        double systemCpuLoad,
        double processCpuLoad,
        long availableProcessors
) {
}
