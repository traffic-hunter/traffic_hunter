package ygo.traffichunter.agent.event;

import java.time.Instant;
import java.util.EventObject;

/**
 * @author yungwang-o
 * @version 1.0.0
 */
public abstract class AgentEvent extends EventObject {

    private final long timestamp;

    public AgentEvent(final Object source) {
        super(source);
        this.timestamp = System.currentTimeMillis();
    }

    public AgentEvent(final Object source, final Instant instant) {
        super(source);
        this.timestamp = instant.toEpochMilli();
    }

    public final long getTimestamp() {
        return this.timestamp;
    }
}
