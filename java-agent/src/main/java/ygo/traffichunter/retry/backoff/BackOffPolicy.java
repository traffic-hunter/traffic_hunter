package ygo.traffichunter.retry.backoff;

import ygo.traffichunter.retry.backoff.policy.ExponentialBackOffPolicy;
import ygo.traffichunter.retry.backoff.policy.FixedBackOffPolicy;

/**
 * The {@code BackOffPolicy} class serves as the base class for defining backoff strategies
 * used in retry mechanisms. It provides common properties such as the interval and multiplier,
 * which can be extended for specific backoff behaviors.
 *
 * <p>Subclasses:</p>
 * <ul>
 *     <li>{@link ExponentialBackOffPolicy}: Implements exponential backoff strategy.</li>
 *     <li>{@link FixedBackOffPolicy}: Implements fixed interval backoff strategy.</li>
 * </ul>
 *
 * @author yungwang-o
 * @version 1.0.0
 */
public class BackOffPolicy {

    private final long intervalMillis;
    private final int multiplier;

    public BackOffPolicy(final long intervalMillis, final int multiplier) {
        this.intervalMillis = intervalMillis;
        this.multiplier = multiplier;
    }

    public long getIntervalMillis() {
        return intervalMillis;
    }

    public int getMultiplier() {
        return multiplier;
    }

    @Override
    public String toString() {
        return "BackOffPolicy{" +
                "intervalMillis=" + intervalMillis +
                ", multiplier=" + multiplier +
                '}';
    }
}
