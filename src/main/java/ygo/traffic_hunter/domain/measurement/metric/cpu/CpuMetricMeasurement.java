package ygo.traffic_hunter.domain.measurement.metric.cpu;

public record CpuMetricMeasurement(
        double systemCpuLoad,
        double processCpuLoad,
        long availableProcessors
) {
}
