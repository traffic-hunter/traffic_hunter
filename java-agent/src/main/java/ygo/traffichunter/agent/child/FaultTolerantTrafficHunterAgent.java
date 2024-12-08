package ygo.traffichunter.agent.child;

import java.util.logging.Logger;
import ygo.traffichunter.agent.TrafficHunterAgent;
import ygo.traffichunter.agent.property.TrafficHunterAgentProperty;
import ygo.traffichunter.retry.backoff.BackOffPolicy;

/**
 * The {@code FaultTolerantTrafficHunterAgent} class extends the functionality of
 * {@link TrafficHunterAgent} by adding fault-tolerant capabilities, such as retry logic
 * and back-off policies. This class is designed to handle scenarios where failures
 * may occur and ensures that the agent can recover gracefully.
 *
 * <p>Example Usage:</p>
 * <pre>{@code
 * TrafficHunterProperty property = TrafficHunterAgent.connect("localhost:8080")
 *     .name("ResilientAgent")
 *     .targetUri("localhost:8888")
 *     .locationJar("/path/to/agent.jar")
 *     .scheduleInterval(10)
 *     .scheduleTimeUnit(TimeUnit.MINUTES)
 *     .faultTolerant()
 *     .retry(5) // Set maximum retry attempts to 5
 *     .backOffPolicy(new ExponentialBackOffPolicy()) // Use exponential back-off policy
 *     .complete();
 * }</pre>
 *
 * @see TrafficHunterAgent
 * @see BackOffPolicy
 * @author yungwang-o
 * @version 1.0.0
 */
public class FaultTolerantTrafficHunterAgent extends TrafficHunterAgent {

    private static final Logger log = Logger.getLogger(FaultTolerantTrafficHunterAgent.class.getName());
    private BackOffPolicy backOffPolicy;
    private int maxAttempt;

    public FaultTolerantTrafficHunterAgent(final TrafficHunterAgent trafficHunterAgent) {
        super(trafficHunterAgent);
    }

    public FaultTolerantTrafficHunterAgent retry(final int maxAttempt) {
        this.maxAttempt = maxAttempt;
        return this;
    }

    public FaultTolerantTrafficHunterAgent backOffPolicy(final BackOffPolicy backOffPolicy) {
        this.backOffPolicy = backOffPolicy;
        return this;
    }

    @Override
    public TrafficHunterAgentProperty complete() {
        return new TrafficHunterAgentProperty(
                this.name,
                this.targetUri,
                this.jar,
                this.scheduleInterval,
                this.uri,
                this.timeUnit,
                maxAttempt,
                backOffPolicy
        );
    }
}
