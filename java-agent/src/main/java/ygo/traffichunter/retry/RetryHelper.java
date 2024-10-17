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
    private Predicate<Throwable> retryPredicate;
    private String retryName;
    private boolean isCheck = false;

    private RetryHelper(final BackOffPolicy backOffPolicy, final int maxAttempts) {
        this.backOffPolicy = backOffPolicy;
        this.maxAttempts = maxAttempts;
    }

    public static RetryHelper start(final BackOffPolicy backOffPolicy, final int maxAttempts) {
        return new RetryHelper(backOffPolicy, maxAttempts);
    }

    public RetryHelper throwable(final Predicate<Throwable> retryPredicate) {
        this.retryPredicate = retryPredicate;

        return this;
    }

    public RetryHelper retryName(final String retryName) {
        this.retryName = retryName;

        return this;
    }

    public RetryHelper failAfterMaxAttempts(final boolean isCheck) {
        this.isCheck = isCheck;

        return this;
    }

    public <T> T retrySupplier(final Supplier<T> supplier) {

        final RetryConfig retryConfig = RetryConfig.custom()
                .maxAttempts(maxAttempts)
                .retryOnException(retryPredicate)
                .failAfterMaxAttempts(isCheck)
                .intervalFunction(IntervalFunction.ofExponentialRandomBackoff(
                        backOffPolicy.getIntervalMillis(),
                        backOffPolicy.getMultiplier())
                )
                .build();

        final Retry retry = Retry.of(retryName, retryConfig);

        return Retry.decorateSupplier(retry, supplier).get();

    }
}
