package org.traffichunter.javaagent.retry.executor;

import io.github.resilience4j.core.IntervalFunction;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.event.RetryOnRetryEvent;
import java.time.Duration;
import org.traffichunter.javaagent.retry.RetryHelper;

public class RetryExecutor {

    private final Retry retry;

    private RetryExecutor(final Retry retry) {
        this.retry = retry;
    }

    public static RetryExecutor of(final RetryHelper retryHelper) {

        RetryConfig config = RetryConfig.custom()
                .maxAttempts(retryHelper.getMaxAttempts())
                .retryOnException(retryHelper.getRetryPredicate())
                .failAfterMaxAttempts(retryHelper.isCheck())
                .intervalFunction(IntervalFunction.ofExponentialRandomBackoff(
                        retryHelper.getBackOffPolicy().getIntervalMillis(),
                        retryHelper.getBackOffPolicy().getMultiplier()
                )).build();

        return new RetryExecutor(Retry.of(retryHelper.getRetryName(), config));
    }

    public void retryEventPublisher(final RetryEventConsumer<RetryEvent> event) {
        retry.getEventPublisher()
                .onRetry(e -> event.consume(new RetryEvent(e)));
    }

    public Runnable execute(final Runnable runnable) {
        return Retry.decorateRunnable(retry, runnable);
    }

    @FunctionalInterface
    public interface RetryEventConsumer<E> {
        void consume(final E event);
    }

    public static class RetryEvent {

        private final RetryOnRetryEvent event;

        public RetryEvent(final RetryOnRetryEvent event) {
            this.event = event;
        }

        public String eventName() {
            return event.getName();
        }

        public int numberOfAttempts() {
            return event.getNumberOfRetryAttempts();
        }

        public Throwable lastThrowable() {
            return event.getLastThrowable();
        }

        public Duration waitInterval() {
            return event.getWaitInterval();
        }
    }
}
