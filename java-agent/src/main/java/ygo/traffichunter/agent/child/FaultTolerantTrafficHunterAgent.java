package ygo.traffichunter.agent.child;

import java.net.URI;
import java.util.logging.Logger;
import ygo.traffichunter.agent.TrafficHunterAgent;
import ygo.traffichunter.agent.property.TrafficHunterAgentProperty;
import ygo.traffichunter.retry.backoff.BackOffPolicy;

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
                this.targetJVMPath,
                this.jar,
                this.scheduleInterval,
                this.uri,
                this.timeUnit,
                maxAttempt,
                backOffPolicy
        );
    }
}
