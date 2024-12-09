package ygo.traffic_hunter.domain.metric.gc.time;

/**
 * @author yungwang-o
 * @version 1.0.0
 */
public record GCMetricCollectionTime(
        long getCollectionCount,
        long getCollectionTime
) {
}
