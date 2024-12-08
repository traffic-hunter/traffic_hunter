package ygo.traffichunter.retry.backoff.policy;

import ygo.traffichunter.retry.backoff.BackOffPolicy;

/**
 * @author yungwang-o
 * @version 1.0.0
 */
public class ExponentialBackOffPolicy extends BackOffPolicy {

    public static final ExponentialBackOffPolicy DEFAULT = new ExponentialBackOffPolicy(1000, 2);

    public ExponentialBackOffPolicy(final long intervalMillis, final int multiplier) {
        super(intervalMillis, multiplier);
    }
}
