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
    private final URI uri;

    public FaultTolerantTrafficHunterAgent(final URI uri) {
        super(uri);
        this.uri = uri;
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
                super.targetJVMPath,
                super.scheduleInterval,
                uri,
                super.timeUnit,
                maxAttempt,
                backOffPolicy
        );
    }
}
