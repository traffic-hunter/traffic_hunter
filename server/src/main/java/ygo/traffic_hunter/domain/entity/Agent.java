package ygo.traffic_hunter.domain.entity;

import java.time.Instant;
import lombok.Builder;

/**
 * @author yungwang-o
 * @version 1.0.0
 */
@Builder
public record Agent(

    Integer id,

    String agentId,

    String agentName,

    String agentVersion,

    Instant agentBootTime
) {

    public static Agent create(final String agentId,
                               final String agentName,
                               final String agentVersion,
                               final Instant agentBootTime) {

        return Agent.builder()
                .agentId(agentId)
                .agentName(agentName)
                .agentVersion(agentVersion)
                .agentBootTime(agentBootTime)
                .build();
    }

    public static Agent create(final Integer id,
                               final String agentId,
                               final String agentName,
                               final String agentVersion,
                               final Instant agentBootTime) {

        return Agent.builder()
                .id(id)
                .agentId(agentId)
                .agentName(agentName)
                .agentVersion(agentVersion)
                .agentBootTime(agentBootTime)
                .build();
    }
}

