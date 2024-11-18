package ygo.traffic_hunter.dto.measurement.metadata;

import java.time.Instant;
import ygo.traffic_hunter.dto.systeminfo.metadata.AgentStatus;

public record Metadata(

        String agentId,

        String agentName,

        String agentVersion,

        Instant agentBootTime,

        AgentStatus agentStatus
) {
}
