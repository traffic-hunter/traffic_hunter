package ygo.traffic_hunter.domain.metric.gc.time;

public record GCMetricCollectionTime(
        long getCollectionCount,
        long getCollectionTime
) {
}
