package ygo.traffic_hunter.domain.metric.memory.usage;

public record MemoryMetricUsage(

        long init,

        long used,

        long committed,

        long max
) {
}
