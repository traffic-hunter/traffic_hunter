package ygo.traffic_hunter.common.map;

import ygo.traffic_hunter.core.dto.request.metadata.AgentMetadata;
import ygo.traffic_hunter.domain.entity.Agent;

public interface AgentMapper {

    Agent map(AgentMetadata metadata);
}
