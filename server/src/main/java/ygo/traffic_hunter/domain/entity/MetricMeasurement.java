package ygo.traffic_hunter.domain.entity;

import java.time.Instant;
import ygo.traffic_hunter.domain.metric.MetricData;

/**
 * @author yungwang-o
 * @version 1.0.0
 */
public record MetricMeasurement(

        Instant time,

        Integer agentId,

        MetricData metricData
) {
}
