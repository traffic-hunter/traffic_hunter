package ygo.traffichunter.agent.engine.systeminfo.metadata;

import java.time.LocalDateTime;
import ygo.traffichunter.agent.AgentStatus;

public class AgentMetadata {

    private final String agentId;

    private final String agentVersion;

    private final String agentName;

    private final LocalDateTime startTime;

    private AgentStatus status;

    public AgentMetadata(final String agentId,
                         final String agentVersion,
                         final String agentName,
                         final LocalDateTime startTime,
                         final AgentStatus status) {

        this.agentId = agentId;
        this.agentVersion = agentVersion;
        this.agentName = agentName;
        this.startTime = startTime;
        this.status = status;
    }

    public String getAgentId() {
        return agentId;
    }

    public String getAgentVersion() {
        return agentVersion;
    }

    public String getAgentName() {
        return agentName;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public AgentStatus getStatus() {
        return status;
    }

    public void setStatus(final AgentStatus status) {
        this.status = status;
    }
}
