package ygo.traffichunter.agent.property;

import java.util.concurrent.TimeUnit;
import ygo.traffichunter.retry.backoff.BackOffPolicy;

public record TrafficHunterAgentProperty(
        String targetJVMPath,
        String jar,
        int scheduleInterval,
        String uri,
        TimeUnit timeUnit,
        int maxAttempt,
        BackOffPolicy backOffPolicy
) {

    public TrafficHunterAgentProperty(final String targetJVMPath,
                                      final String jar,
                                      final int scheduleInterval,
                                      final String uri,
                                      final TimeUnit timeUnit,
                                      final int maxAttempt,
                                      final BackOffPolicy backOffPolicy) {

        this.targetJVMPath = targetJVMPath;
        this.jar = jar;
        this.scheduleInterval = scheduleInterval;
        this.uri = uri;
        this.timeUnit = timeUnit;
        this.maxAttempt = maxAttempt;
        this.backOffPolicy = backOffPolicy;
    }

    public TrafficHunterAgentProperty(final String targetJVMPath,
                                      final String jar,
                                      final int scheduleInterval,
                                      final String uri,
                                      final TimeUnit timeUnit) {

        this(targetJVMPath, jar, scheduleInterval, uri, timeUnit, 0, null);
    }

    @Override
    public String toString() {
        return "TrafficHunterAgentProperty{" +
                "targetJVMPath='" + targetJVMPath + '\'' +
                ", jar=" + jar +
                ", scheduleInterval=" + scheduleInterval +
                ", uri=" + uri +
                ", timeUnit=" + timeUnit +
                ", maxAttempt=" + maxAttempt +
                ", backOffPolicy=" + backOffPolicy.toString() +
                '}';
    }
}
