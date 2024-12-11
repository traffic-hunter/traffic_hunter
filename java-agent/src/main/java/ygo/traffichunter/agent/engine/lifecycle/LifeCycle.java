package ygo.traffichunter.agent.engine.lifecycle;

import java.time.Duration;
import java.time.Instant;

/**
 * The {@code LifeCycle} abstract class defines a common structure for managing
 * the lifecycle of an object, including its start time, end time, and uptime.
 *
 * <p>Features:</p>
 * <ul>
 *     <li>Automatically initializes the start time when an instance is created.</li>
 *     <li>Provides abstract methods for retrieving the start time, end time, and uptime.</li>
 *     <li>Allows subclasses to implement specific behaviors for managing lifecycle information.</li>
 * </ul>
 *
 * <p>Usage:</p>
 * <pre>{@code
 * public class TaskLifeCycle extends LifeCycle {
 *
 *     @Override
 *     public Instant getStartTime() {
 *         return startTime;
 *     }
 *
 *     @Override
 *     public Instant getEndTime() {
 *         if (endTime == null) {
 *             endTime = Instant.now();
 *         }
 *         return endTime;
 *     }
 *
 *     @Override
 *     public Duration getUpTime() {
 *         return Duration.between(getStartTime(), getEndTime());
 *     }
 * }
 *
 * TaskLifeCycle task = new TaskLifeCycle();
 * System.out.println("Uptime: " + task.getUpTime().toSeconds() + " seconds");
 * }</pre>
 *
 * @see Instant
 * @see Duration
 *
 * @author yungwang-o
 * @version 1.0.0
*/
public abstract class LifeCycle {

    protected final Instant startTime;

    protected Instant endTime;

    protected LifeCycle() {
        this.startTime = Instant.now();
    }

    public abstract Instant getStartTime();

    public abstract Instant getEndTime();

    public abstract Double getUpTime();
}
