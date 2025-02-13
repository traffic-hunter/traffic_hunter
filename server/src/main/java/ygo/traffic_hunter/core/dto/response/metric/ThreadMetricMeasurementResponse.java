package ygo.traffic_hunter.core.dto.response.metric;

public record ThreadMetricMeasurementResponse(
        int threadCount,

        int getPeekThreadCount,

        long getTotalStartThreadCount
) {
}
