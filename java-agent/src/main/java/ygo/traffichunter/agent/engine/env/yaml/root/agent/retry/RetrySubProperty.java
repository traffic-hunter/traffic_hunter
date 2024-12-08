package ygo.traffichunter.agent.engine.env.yaml.root.agent.retry;

import ygo.traffichunter.agent.engine.env.yaml.root.agent.retry.backoff.BackOffSubProperty;

/**
 * @author yungwang-o
 * @version 1.0.0
 */
public class RetrySubProperty {

    private int maxAttempt;
    private BackOffSubProperty backoff;

    public RetrySubProperty() {
    }

    public RetrySubProperty(final int maxAttempt, final BackOffSubProperty backoff) {
        this.maxAttempt = maxAttempt;
        this.backoff = backoff;
    }

    public int getMaxAttempt() {
        return maxAttempt;
    }

    public void setMaxAttempt(final int maxAttempt) {
        this.maxAttempt = maxAttempt;
    }

    public BackOffSubProperty getBackoff() {
        return backoff;
    }

    public void setBackoff(final BackOffSubProperty backoff) {
        this.backoff = backoff;
    }

    @Override
    public String toString() {
        return "RetrySubProperty{" +
                "maxAttempt=" + maxAttempt +
                ", backoff=" + backoff +
                '}';
    }
}
