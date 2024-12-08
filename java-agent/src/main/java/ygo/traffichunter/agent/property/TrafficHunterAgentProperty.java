package ygo.traffichunter.agent.property;

import java.util.concurrent.TimeUnit;
import ygo.traffichunter.retry.backoff.BackOffPolicy;

/**
 * @author yungwang-o
 * @version 1.0.0
 */
public record TrafficHunterAgentProperty(
        String name,
        String targetUri,
        String jar,
        int scheduleInterval,
        String serverUri,
        TimeUnit timeUnit,
        int maxAttempt,
        BackOffPolicy backOffPolicy
) {

    public TrafficHunterAgentProperty(final String name,
                                      final String targetUri,
                                      final String jar,
                                      final int scheduleInterval,
                                      final String serverUri,
                                      final TimeUnit timeUnit,
                                      final int maxAttempt,
                                      final BackOffPolicy backOffPolicy) {

        this.name = name;
        this.targetUri = targetUri;
        this.jar = jar;
        this.scheduleInterval = scheduleInterval;
        this.serverUri = serverUri;
        this.timeUnit = timeUnit;
        this.maxAttempt = maxAttempt;
        this.backOffPolicy = backOffPolicy;
    }

    public TrafficHunterAgentProperty(final String name,
                                      final String targetUri,
                                      final String jar,
                                      final int scheduleInterval,
                                      final String serverUri,
                                      final TimeUnit timeUnit) {

        this(name, targetUri, jar, scheduleInterval, serverUri, timeUnit, 0, null);
    }

    @Override
    public String toString() {
        return "TrafficHunterAgentProperty{" +
                "name='" + name + '\'' +
                ", targetUri='" + targetUri + '\'' +
                ", jar=" + jar +
                ", scheduleInterval=" + scheduleInterval +
                ", serverUri=" + serverUri +
                ", timeUnit=" + timeUnit +
                ", maxAttempt=" + maxAttempt +
                ", backOffPolicy=" + backOffPolicy.toString() +
                '}';
    }
}
