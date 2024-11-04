package ygo.traffichunter.agent.engine.lifecycle;

import java.time.Duration;
import java.time.Instant;

public abstract class LifeCycle {

    protected final Instant startTime;

    protected volatile Instant endTime;

    protected LifeCycle() {
        this.startTime = Instant.now();
    }

    public abstract Instant getStartTime();

    public abstract Instant getEndTime();

    public abstract Duration getUpTime();
}
