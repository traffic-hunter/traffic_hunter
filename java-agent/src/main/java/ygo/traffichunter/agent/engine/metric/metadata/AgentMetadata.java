package ygo.traffichunter.agent.engine.metric.metadata;

import java.time.Instant;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import ygo.traffichunter.agent.AgentStatus;
import ygo.traffichunter.agent.event.listener.AgentStateEventListener;
import ygo.traffichunter.agent.event.object.AgentStateEvent;

public record AgentMetadata(
        String agentId,
        String agentVersion,
        String agentName,
        Instant startTime,
        AtomicReference<AgentStatus> status
) implements AgentStateEventListener {

    @Override
    public void onEvent(final AgentStateEvent event) {
        this.setStatus(event.getAfterStatus());
    }

    private void setStatus(final AgentStatus status) {
        this.status.set(status);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AgentMetadata that = (AgentMetadata) o;
        return Objects.equals(agentId, that.agentId) &&
                Objects.equals(agentVersion, that.agentVersion) &&
                Objects.equals(agentName, that.agentName) &&
                Objects.equals(startTime, that.startTime) &&
                Objects.equals(status.get(), that.status.get());
    }

    @Override
    public int hashCode() {
        return Objects.hash(agentId, agentVersion, agentName, startTime, status.get());
    }
}
