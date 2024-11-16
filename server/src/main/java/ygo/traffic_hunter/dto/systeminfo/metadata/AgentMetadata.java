package ygo.traffic_hunter.dto.systeminfo.metadata;

import java.time.Instant;

public record AgentMetadata(
        String agentId,
        String agentVersion,
        String agentName,
        Instant startTime,
        AgentStatus status
) {
}
