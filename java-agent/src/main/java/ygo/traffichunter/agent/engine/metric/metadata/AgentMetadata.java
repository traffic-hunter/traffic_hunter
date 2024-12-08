package ygo.traffichunter.agent.engine.metric.metadata;

import java.time.Instant;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import ygo.traffichunter.agent.AgentStatus;
import ygo.traffichunter.agent.event.listener.AgentStateEventListener;
import ygo.traffichunter.agent.event.object.AgentStateEvent;

/**
 * The {@code AgentMetadata} record represents metadata for an agent and acts as an
 * {@link AgentStateEventListener} to respond to state change events.
 *
 * <p>Features:</p>
 * <ul>
 *     <li>Stores immutable metadata fields such as agent ID, version, name, and start time.</li>
 *     <li>Maintains a mutable {@link AtomicReference} for the agent's current status.</li>
 *     <li>Implements {@link AgentStateEventListener} to react to state change events.</li>
 *     <li>Overrides {@code equals} and {@code hashCode} to handle mutable status appropriately.</li>
 * </ul>
 *
 * @see AgentStateEventListener
 * @see AgentStateEvent
 * @see AgentStatus
 * @see AtomicReference
 *
 * @author yungwang-o
 * @version 1.0.0
 */
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
