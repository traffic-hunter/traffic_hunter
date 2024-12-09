package ygo.traffic_hunter.core.dto.request.metadata;

import java.time.Instant;

/**
 * @author yungwang-o
 * @version 1.0.0
 */
public record AgentMetadata(
        String agentId,
        String agentVersion,
        String agentName,
        Instant startTime,
        AgentStatus status
) {
}
