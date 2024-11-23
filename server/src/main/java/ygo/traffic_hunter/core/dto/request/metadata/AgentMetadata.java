package ygo.traffic_hunter.core.dto.request.metadata;

import java.time.Instant;

public record AgentMetadata(
        String agentId,
        String agentVersion,
        String agentName,
        Instant startTime,
        AgentStatus status
) {
}
