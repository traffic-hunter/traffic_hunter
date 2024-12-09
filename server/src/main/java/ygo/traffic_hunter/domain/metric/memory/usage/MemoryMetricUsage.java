package ygo.traffic_hunter.domain.metric.memory.usage;

/**
 * @author yungwang-o
 * @version 1.0.0
 */
public record MemoryMetricUsage(

        long init,

        long used,

        long committed,

        long max
) {
}
