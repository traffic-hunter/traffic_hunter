package ygo.traffichunter.retry.backoff.policy;

import ygo.traffichunter.retry.backoff.BackOffPolicy;

public class ExponentialBackOffPolicy extends BackOffPolicy {

    public ExponentialBackOffPolicy(final long intervalMillis, final int multiplier) {
        super(intervalMillis, multiplier);
    }
}
