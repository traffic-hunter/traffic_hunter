package ygo.traffic_hunter.common.map.impl.agent;

import org.springframework.stereotype.Component;
import ygo.traffic_hunter.common.map.AgentMapper;
import ygo.traffic_hunter.core.dto.request.metadata.AgentMetadata;
import ygo.traffic_hunter.domain.entity.Agent;

@Component
public class AgentMapperImpl implements AgentMapper {

    @Override
    public Agent map(final AgentMetadata metadata) {
        return Agent.create(
                metadata.agentId(),
                metadata.agentName(),
                metadata.agentVersion(),
                metadata.startTime()
                );
    }
}
