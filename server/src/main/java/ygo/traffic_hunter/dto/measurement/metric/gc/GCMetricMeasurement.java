package ygo.traffic_hunter.dto.measurement.metric.gc;

import java.util.List;
import ygo.traffic_hunter.dto.measurement.metric.gc.time.GCMetricCollectionTime;

public record GCMetricMeasurement(List<GCMetricCollectionTime> gcMetricCollectionTimes) {
}
