/**
 * The MIT License
 *
 * Copyright (c) 2024 yungwang-o
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
package org.traffichunter.javaagent.bootstrap.engine.sender.manager;

import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.traffichunter.javaagent.bootstrap.engine.context.AgentExecutableContext;
import org.traffichunter.javaagent.bootstrap.engine.property.TrafficHunterAgentProperty;
import org.traffichunter.javaagent.bootstrap.engine.sender.websocket.AgentSystemMetricSender;
import org.traffichunter.javaagent.bootstrap.engine.sender.websocket.AgentTransactionMetricSender;
import org.traffichunter.javaagent.bootstrap.metadata.AgentMetadata;
import org.traffichunter.javaagent.commons.status.AgentStatus;
import org.traffichunter.javaagent.commons.util.AgentUtil;
import org.traffichunter.javaagent.retry.RetryHelper;
import org.traffichunter.javaagent.websocket.MetricWebSocketClient;
import org.traffichunter.javaagent.websocket.metadata.Metadata;

/**
 * The {@code MetricSendSessionManager} class manages the session for sending
 * transaction and system metrics from the TrafficHunter Agent to a server.
 * It handles WebSocket connections, retries, scheduling, and lifecycle management
 * of the metric-sending operations.
 *
 * <p>Purpose:</p>
 * <ul>
 *     <li>Manages WebSocket connections for real-time metric transmission.</li>
 *     <li>Schedules periodic system metric transmissions using a {@link ScheduledExecutorService}.</li>
 *     <li>Retries connection attempts in case of failures with a configurable back-off policy.</li>
 *     <li>Provides lifecycle management for starting and stopping the session.</li>
 * </ul>
 *
 * <p>Key Features:</p>
 * <ul>
 *     <li>{@code run()} - Starts the metric sending session, ensuring retries and scheduling are properly configured.</li>
 *     <li>{@code close()} - Safely shuts down the session, releasing all resources.</li>
 *     <li>Integrates with {@link Retry} to handle WebSocket reconnections in case of failures.</li>
 * </ul>
 *
 * <p>Thread Management:</p>
 * <ul>
 *     <li>Uses a {@link ScheduledExecutorService} for scheduling periodic system metrics.</li>
 *     <li>Uses a {@link ExecutorService} for running transaction metric transmissions.</li>
 *     <li>Both executors are safely shut down during the {@code close()} method.</li>
 * </ul>
 *
 * <p>Limitations:</p>
 * <ul>
 *     <li>If the agent's context status is already {@code RUNNING}, the session cannot be restarted without stopping it first.</li>
 *     <li>Retries only handle specific exceptions, as configured in the {@link RetryHelper}.</li>
 * </ul>
 *
 * @see Retry
 * @see ScheduledExecutorService
 * @see MetricWebSocketClient
 * @see TrafficHunterAgentProperty
 * @see AgentExecutableContext
 * @see AgentMetadata
 * @see RetryHelper
 * @author yungwang-o
 * @version 1.0.0
 */
public class MetricSendSessionManager {

    private static final Logger log = LoggerFactory.getLogger(MetricSendSessionManager.class);

    private final TrafficHunterAgentProperty property;

    private final AgentTransactionMetricSender transactionMetricSender;

    private final AgentSystemMetricSender systemMetricSender;

    private final ScheduledExecutorService schedule;

    private final ExecutorService executor;

    private final AgentExecutableContext context;

    private final AgentMetadata metadata;

    private final MetricWebSocketClient client;

    public MetricSendSessionManager(final TrafficHunterAgentProperty property,
                                    final AgentExecutableContext context,
                                    final AgentMetadata metadata) {

        this.client = initializeWebsocket(property, metadata);
        this.client.connect();
        this.metadata = metadata;
        this.context = context;
        this.property = property;
        this.transactionMetricSender = new AgentTransactionMetricSender(client);
        this.systemMetricSender = new AgentSystemMetricSender(client, property);
        this.schedule = Executors.newSingleThreadScheduledExecutor(getThreadFactory("TransactionSystemInfoMetricSender"));
        this.executor = Executors.newVirtualThreadPerTaskExecutor();
    }

    public void run() {

        if(context.isStopped()) {
            log.error("MetricSendSessionManager is stopped");
            return;
        }

        if(context.isRunning()) {
            log.error("MetricSendSessionManager is already running");
            return;
        }

        log.info("start Metric send!!");

        RetryHelper retryHelper = RetryHelper.builder()
                .backOffPolicy(property.backOffPolicy())
                .isCheck(true)
                .retryName("websocket retry")
                .maxAttempts(property.maxAttempt())
                .retryPredicate(throwable -> throwable instanceof IllegalStateException)
                .build();

        RetryConfig retryConfig = retryHelper.configureRetry();

        Retry retry = Retry.of(retryHelper.getRetryName(), retryConfig);

        retry.getEventPublisher()
                        .onRetry(event -> {
                            client.reconnect();
                            log.info("{} retry {} attempts...", event.getName(), event.getNumberOfRetryAttempts());
                        });

        context.setStatus(AgentStatus.RUNNING);

        executor.execute(Retry.decorateRunnable(retry, () -> transactionMetricSender.toSend(metadata)));

        schedule.scheduleWithFixedDelay(Retry.decorateRunnable(retry, () -> systemMetricSender.toSend(metadata)),
                0,
                property.scheduleInterval(),
                property.timeUnit()
        );
    }

    public void close() {
        log.info("closing MetricSendSessionManager...");
        context.setStatus(AgentStatus.EXIT);
        client.close();
        executor.shutdown();
        schedule.shutdown();
    }

    private static MetricWebSocketClient initializeWebsocket(final TrafficHunterAgentProperty property,
                                                             final AgentMetadata metadata) {
        return new MetricWebSocketClient(
                AgentUtil.WEBSOCKET_URL.getUri(property.serverUri()),
                Metadata.builder()
                        .agentId(metadata.agentId())
                        .agentVersion(metadata.agentVersion())
                        .agentName(metadata.agentName())
                        .startTime(metadata.startTime())
                        .status(metadata.status().get())
                        .build()
        );
    }

    private ThreadFactory getThreadFactory(final String threadName) {
        return r -> {
            Thread thread = new Thread(r);
            thread.setDaemon(true);
            thread.setName(threadName);

            return thread;
        };
    }
}
