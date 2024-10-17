package ygo.traffichunter.retry.backoff.policy;

import ygo.traffichunter.retry.backoff.BackOffPolicy;

public class FixedBackOffPolicy extends BackOffPolicy {

    public static final FixedBackOffPolicy DEFAULT = new FixedBackOffPolicy(1000);

    public FixedBackOffPolicy(final long intervalMillis) {
        super(intervalMillis, 1);
    }
}
