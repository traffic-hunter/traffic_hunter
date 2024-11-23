package ygo.traffic_hunter.domain.metric.cpu;

public record CpuMetricMeasurement(
        double systemCpuLoad,
        double processCpuLoad,
        long availableProcessors
) {
}
