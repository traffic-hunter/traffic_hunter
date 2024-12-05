package ygo.traffic_hunter.domain.entity;

import java.time.Instant;
import ygo.traffic_hunter.domain.metric.MetricData;

public record MetricMeasurement(

        Instant time,

        Integer agentId,

        MetricData metricData
) {
}
