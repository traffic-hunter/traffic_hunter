package ygo.traffic_hunter.core.repository;

import java.util.List;
import ygo.traffic_hunter.domain.entity.Agent;

public interface AgentRepository {

    void save(Agent agent);

    Agent findById(Integer id);

    Agent findByAgentName(String agentName);

    Agent findByAgentId(String agentId);

    List<Agent> findAll();

    boolean existsByAgentId(String agentId);

    boolean existsByAgentName(String agentName);
}
