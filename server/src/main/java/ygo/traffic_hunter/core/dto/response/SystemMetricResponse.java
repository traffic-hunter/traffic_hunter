package ygo.traffic_hunter.core.dto.response;

import java.time.Instant;
import ygo.traffic_hunter.domain.metric.MetricData;

/**
 * @author yungwang-o
 * @version 1.0.0
 */
public record SystemMetricResponse(
        Instant time,
        String agentName,
        Instant agentBootTime,
        String agentVersion,
        MetricData metricData
) {

    public static SystemMetricResponse create(final Instant time,
                                              final String agentName,
                                              final Instant agentBootTime,
                                              final String agentVersion,
                                              final MetricData metricData) {

        return new SystemMetricResponse(time, agentName, agentBootTime, agentVersion, metricData);
    }
}
