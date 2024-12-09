package ygo.traffic_hunter.domain.metric.gc;

import java.util.List;
import ygo.traffic_hunter.domain.metric.gc.time.GCMetricCollectionTime;

/**
 * @author yungwang-o
 * @version 1.0.0
 */
public record GCMetricMeasurement(List<GCMetricCollectionTime> gcMetricCollectionTimes) {
}
