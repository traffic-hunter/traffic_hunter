package ygo.traffic_hunter.core.dto.response.metric;

public record CpuMetricMeasurementResponse(
        double systemCpuLoad,
        double processCpuLoad,
        long availableProcessors
) {
}
