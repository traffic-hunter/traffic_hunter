package ygo.traffic_hunter.core.dto.response;

import ygo.traffic_hunter.domain.metric.MetricData;

public record SystemMetricResponse(String agentName, MetricData metricData) {
}
