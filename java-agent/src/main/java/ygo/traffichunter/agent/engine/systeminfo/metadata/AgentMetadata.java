package ygo.traffichunter.agent.engine.systeminfo.metadata;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicReference;
import ygo.traffichunter.agent.AgentStatus;
import ygo.traffichunter.agent.event.listener.AgentStateEventListener;
import ygo.traffichunter.agent.event.object.AgentStateEvent;

public class AgentMetadata implements AgentStateEventListener {

    private final String agentId;

    private final String agentVersion;

    private final String agentName;

    private final Instant startTime;

    private final AtomicReference<AgentStatus> status;

    public AgentMetadata(final String agentId, final String agentVersion, final String agentName,
                         final Instant startTime, final AgentStatus status) {
        this.agentId = agentId;
        this.agentVersion = agentVersion;
        this.agentName = agentName;
        this.startTime = startTime;
        this.status = new AtomicReference<>(status);
    }

    @Override
    public void onEvent(final AgentStateEvent event) {
        this.setStatus(event.getAfterStatus());
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

    public Instant getStartTime() {
        return startTime;
    }

    public AgentStatus getStatus() {
        return status.get();
    }

    private void setStatus(final AgentStatus status) {
        this.status.set(status);
    }
}
