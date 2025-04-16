/*
 * The MIT License
 *
 * Copyright (c) 2024 traffic-hunter.org
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.traffichunter.javaagent.websocket;

import java.io.Closeable;
import java.net.URI;
import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.logging.Logger;
import org.traffichunter.javaagent.commons.type.MetricType;
import org.traffichunter.javaagent.retry.RetryHelper;
import org.traffichunter.javaagent.retry.backoff.BackOffPolicy;
import org.traffichunter.javaagent.retry.executor.RetryExecutor;
import org.traffichunter.javaagent.websocket.metadata.Metadata;

/**
 * @author yungwang-o
 * @version 1.1.0
 */
public final class TrafficHunterWebsocketClient implements Closeable {

    private static final Logger log = Logger.getLogger(TrafficHunterWebsocketClient.class.getName());

    private final MetricWebSocketClient delegator;

    private final ExecutorService executorService;

    private final RetryExecutor retryExecutor;

    private TrafficHunterWebsocketClient(final Builder builder) {

        this.delegator = new MetricWebSocketClient(builder.endpoint, builder.metadata);
        this.executorService = Executors.newFixedThreadPool(
                Runtime.getRuntime().availableProcessors() / 2,
                createThreadFactory()
        );
        this.retryExecutor = RetryExecutor.of(
                RetryHelper.builder()
                        .backOffPolicy(builder.backOffPolicy)
                        .isCheck(true)
                        .retryName("websocket retry")
                        .maxAttempts(builder.maxAttempts)
                        .retryPredicate(throwable -> throwable instanceof IllegalStateException)
                        .build()
        );

        this.delegator.connect();
        registerRetryEvent();
    }

    public static Builder builder() {
        return new Builder();
    }

    public <D> void toSend(final D data, final MetricType metricType) {

        executorService.execute(
                retryExecutor.execute(() -> delegator.compressToSend(data, metricType))
        );
    }

    public <D> void toSend(final D data) {

        executorService.execute(
                retryExecutor.execute(() -> delegator.toSend(data))
        );
    }

    public <D> void toSend(final Collection<D> data) {

        executorService.execute(
                retryExecutor.execute(() -> delegator.toSend(data))
        );
    }

    public <D> void toSend(final Collection<D> data, final MetricType metricType) {

        executorService.execute(
                retryExecutor.execute(() -> delegator.compressToSend(data, metricType))
        );
    }

    @Override
    public void close() {
        executorService.shutdown();
        delegator.close();
    }

    private void registerRetryEvent() {
        retryExecutor.retryEventPublisher(event -> {
            delegator.reconnect();
            log.info(event.numberOfAttempts() + " retry Attempt...");
        });
    }

    private static ThreadFactory createThreadFactory() {
        return runnable -> {
            Thread thread = new Thread(runnable);
            thread.setDaemon(true);
            thread.setName(TrafficHunterWebsocketClient.class.getSimpleName());
            return thread;
        };
    }

    public static class Builder {

        private URI endpoint;
        private Metadata metadata;
        private BackOffPolicy backOffPolicy;
        private int maxAttempts;

        private Builder() {}

        public Builder endpoint(final URI endpoint) {
            this.endpoint = Objects.requireNonNull(endpoint);
            return this;
        }

        public Builder metadata(final Metadata metadata) {
            this.metadata = Objects.requireNonNull(metadata);
            return this;
        }

        public Builder backOffPolicy(final BackOffPolicy backOffPolicy) {
            this.backOffPolicy = Objects.requireNonNull(backOffPolicy);
            return this;
        }

        public Builder maxAttempts(final int maxAttempts) {
            this.maxAttempts = maxAttempts;
            return this;
        }

        public TrafficHunterWebsocketClient build() {;
            return new TrafficHunterWebsocketClient(this);
        }
    }
}
