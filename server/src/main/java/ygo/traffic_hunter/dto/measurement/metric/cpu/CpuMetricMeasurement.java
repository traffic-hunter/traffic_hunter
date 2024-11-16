package ygo.traffic_hunter.dto.measurement.metric.cpu;

public record CpuMetricMeasurement(
        double systemCpuLoad,
        double processCpuLoad,
        long availableProcessors
) {
}
