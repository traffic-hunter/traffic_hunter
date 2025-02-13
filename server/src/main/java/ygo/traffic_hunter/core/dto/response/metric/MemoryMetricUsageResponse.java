package ygo.traffic_hunter.core.dto.response.metric;

public record MemoryMetricUsageResponse(
        long init,

        long used,

        long committed,

        long max
) {
}
