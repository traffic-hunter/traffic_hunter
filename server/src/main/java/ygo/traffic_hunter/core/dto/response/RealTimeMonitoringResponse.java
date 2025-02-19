package ygo.traffic_hunter.core.dto.response;

import java.time.Instant;
import java.util.List;
import ygo.traffic_hunter.core.dto.response.metric.SystemMetricResponse;

/**
 * @author JuSeong
 * @version 1.1.0
 */
public record RealTimeMonitoringResponse(String agentName,
                                         Instant agentBootTime,
                                         String agentVersion,
                                         List<SystemMetricResponse> systemMetricResponses
) {

    public static RealTimeMonitoringResponse create(List<SystemMetricResponse> systemMetricResponses) {

        if (systemMetricResponses.isEmpty()) {
            return null;
        }

        SystemMetricResponse systemMetricResponse = systemMetricResponses.getFirst();
        String agentName = systemMetricResponse.agentName();
        Instant agentBootTime = systemMetricResponse.agentBootTime();
        String agentVersion = systemMetricResponse.agentVersion();
        return new RealTimeMonitoringResponse(agentName, agentBootTime, agentVersion, systemMetricResponses);
    }
}
