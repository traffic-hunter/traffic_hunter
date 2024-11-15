package ygo.traffichunter.retry;

import io.github.resilience4j.core.IntervalFunction;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.logging.Logger;
import ygo.traffichunter.retry.backoff.BackOffPolicy;

public class RetryHelper {

    private static final Logger log = Logger.getLogger(RetryHelper.class.getName());

    private final BackOffPolicy backOffPolicy;

    private final int maxAttempts;

    private final Predicate<Throwable> retryPredicate;

    private final String retryName;

    private final boolean isCheck;

    private RetryHelper(final Builder builder) {
        this.backOffPolicy = builder.backOffPolicy;
        this.maxAttempts = builder.maxAttempts;
        this.retryPredicate = builder.retryPredicate;
        this.retryName = builder.retryName;
        this.isCheck = builder.isCheck;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private BackOffPolicy backOffPolicy;

        private int maxAttempts;

        private Predicate<Throwable> retryPredicate;

        private String retryName;

        private boolean isCheck;

        public Builder() {
        }

        public Builder backOffPolicy(final BackOffPolicy backOffPolicy) {
            this.backOffPolicy = backOffPolicy;
            return this;
        }

        public Builder maxAttempts(final int maxAttempts) {
            this.maxAttempts = maxAttempts;
            return this;
        }

        public Builder retryPredicate(final Predicate<Throwable> retryPredicate) {
            this.retryPredicate = retryPredicate;
            return this;
        }

        public Builder retryName(final String retryName) {
            this.retryName = retryName;
            return this;
        }

        public Builder isCheck(final boolean isCheck) {
            this.isCheck = isCheck;
            return this;
        }

        public RetryHelper build() {
            return new RetryHelper(this);
        }
    }

    public BackOffPolicy getBackOffPolicy() {
        return backOffPolicy;
    }

    public int getMaxAttempts() {
        return maxAttempts;
    }

    public Predicate<Throwable> getRetryPredicate() {
        return retryPredicate;
    }

    public String getRetryName() {
        return retryName;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public RetryConfig configureRetry() {
        return RetryConfig.custom()
                .maxAttempts(maxAttempts)
                .retryOnException(retryPredicate)
                .failAfterMaxAttempts(isCheck)
                .intervalFunction(IntervalFunction.ofExponentialRandomBackoff(
                        backOffPolicy.getIntervalMillis(),
                        backOffPolicy.getMultiplier()
                )).build();
    }
}
