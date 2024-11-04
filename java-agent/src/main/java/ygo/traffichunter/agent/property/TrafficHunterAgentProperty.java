package ygo.traffichunter.agent.property;

import java.net.URI;
import java.util.concurrent.TimeUnit;
import ygo.traffichunter.retry.backoff.BackOffPolicy;

public record TrafficHunterAgentProperty(
        String targetJVMPath,
        int scheduleInterval,
        URI uri,
        TimeUnit timeUnit,
        int maxAttempt,
        BackOffPolicy backOffPolicy
) {

    public TrafficHunterAgentProperty(final String targetJVMPath,
                                      final int scheduleInterval,
                                      final URI uri,
                                      final TimeUnit timeUnit,
                                      final int maxAttempt,
                                      final BackOffPolicy backOffPolicy) {

        this.targetJVMPath = targetJVMPath;
        this.scheduleInterval = scheduleInterval;
        this.uri = uri;
        this.timeUnit = timeUnit;
        this.maxAttempt = maxAttempt;
        this.backOffPolicy = backOffPolicy;
    }

    public TrafficHunterAgentProperty(final String targetJVMPath,
                                      final int scheduleInterval,
                                      final URI uri,
                                      final TimeUnit timeUnit) {

        this(targetJVMPath, scheduleInterval, uri, timeUnit, 0, null);
    }

    @Override
    public String toString() {
        return "TrafficHunterAgentProperty{" +
                "targetJVMPath='" + targetJVMPath + '\'' +
                ", scheduleInterval=" + scheduleInterval +
                ", uri=" + uri +
                ", timeUnit=" + timeUnit +
                ", maxAttempt=" + maxAttempt +
                ", backOffPolicy=" + backOffPolicy.toString() +
                '}';
    }
}
