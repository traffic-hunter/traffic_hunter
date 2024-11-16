package ygo.traffic_hunter.dto.measurement.metric.gc.time;

public record GCMetricCollectionTime(
        long getCollectionCount,
        long getCollectionTime
) {
}
