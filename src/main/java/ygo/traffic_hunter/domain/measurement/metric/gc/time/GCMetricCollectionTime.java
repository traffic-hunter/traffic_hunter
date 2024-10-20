package ygo.traffic_hunter.domain.measurement.metric.gc.time;

public record GCMetricCollectionTime(
        long getCollectionCount,
        long getCollectionTime
) {
}
