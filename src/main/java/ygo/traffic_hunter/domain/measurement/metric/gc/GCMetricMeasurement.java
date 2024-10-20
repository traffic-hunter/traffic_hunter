package ygo.traffic_hunter.domain.measurement.metric.gc;

import java.util.List;
import ygo.traffic_hunter.domain.measurement.metric.gc.time.GCMetricCollectionTime;

public record GCMetricMeasurement(List<GCMetricCollectionTime> gcMetricCollectionTimes) {
}
