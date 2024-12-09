package ygo.traffic_hunter.common.map;

import ygo.traffic_hunter.core.dto.request.metadata.AgentMetadata;
import ygo.traffic_hunter.domain.entity.Agent;

/**
 * @author yungwang-o
 * @version 1.0.0
 */
public interface AgentMapper {

    Agent map(AgentMetadata metadata);
}
