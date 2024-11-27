package ygo.traffichunter.agent.engine.metric.metadata;

import java.time.Instant;
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
}
