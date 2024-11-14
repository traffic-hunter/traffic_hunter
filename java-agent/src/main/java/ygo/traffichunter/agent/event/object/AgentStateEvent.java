package ygo.traffichunter.agent.event.object;

import java.time.Instant;
import ygo.traffichunter.agent.AgentStatus;
import ygo.traffichunter.agent.event.AgentEvent;

public class AgentStateEvent extends AgentEvent {

    private final AgentStatus beforeStatus;

    private final AgentStatus afterStatus;

    public AgentStateEvent(final Object source,
                           final AgentStatus beforeStatus,
                           final AgentStatus afterStatus) {

        super(source);
        this.beforeStatus = beforeStatus;
        this.afterStatus = afterStatus;
    }

    public AgentStateEvent(final Object source,
                           final Instant instant,
                           final AgentStatus beforeStatus,
                           final AgentStatus afterStatus) {

        super(source, instant);
        this.beforeStatus = beforeStatus;
        this.afterStatus = afterStatus;
    }

    public AgentStatus getBeforeStatus() {
        return this.beforeStatus;
    }

    public AgentStatus getAfterStatus() {
        return this.afterStatus;
    }
}
