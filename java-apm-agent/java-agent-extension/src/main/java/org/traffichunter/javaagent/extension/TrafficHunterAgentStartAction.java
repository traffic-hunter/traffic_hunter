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
package org.traffichunter.javaagent.extension;

import io.opentelemetry.sdk.OpenTelemetrySdk;
import java.io.Closeable;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.instrument.Instrumentation;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import org.traffichunter.javaagent.bootstrap.BootstrapLogger;
import org.traffichunter.javaagent.bootstrap.Configurations;
import org.traffichunter.javaagent.bootstrap.Configurations.ConfigProperty;
import org.traffichunter.javaagent.bootstrap.LifeCycle;
import org.traffichunter.javaagent.bootstrap.OpenTelemetrySdkBridge;
import org.traffichunter.javaagent.bootstrap.TrafficHunterAgentShutdownHook;
import org.traffichunter.javaagent.bootstrap.TrafficHunterAgentStarter;
import org.traffichunter.javaagent.commons.status.AgentStatus;
import org.traffichunter.javaagent.commons.type.MetricType;
import org.traffichunter.javaagent.commons.util.AgentUtil;
import org.traffichunter.javaagent.extension.banner.AsciiBanner;
import org.traffichunter.javaagent.extension.env.ConfigurableEnvironment;
import org.traffichunter.javaagent.extension.env.yaml.YamlConfigurableEnvironment;
import org.traffichunter.javaagent.extension.metadata.AgentMetadata;
import org.traffichunter.javaagent.extension.metadata.MetadataWrapper;
import org.traffichunter.javaagent.extension.property.TrafficHunterAgentProperty;
import org.traffichunter.javaagent.jmx.JmxMetricSender;
import org.traffichunter.javaagent.jmx.metric.systeminfo.SystemInfo;
import org.traffichunter.javaagent.websocket.TrafficHunterWebsocketClient;
import org.traffichunter.javaagent.websocket.metadata.Metadata;

/**
 * <p>
 *     The {@code TrafficHunterAgentStartAction} class is responsible for initializing and starting the TrafficHunter Agent.
 *     It serves as the entry point for setting up the agent's execution environment, managing its lifecycle,
 *     and orchestrating its core functionalities.
 * </p>
 * @author yungwang-o
 * @version 1.1.0
 */
@SuppressWarnings("unused")
public final class TrafficHunterAgentStartAction implements TrafficHunterAgentStarter {

    private static final BootstrapLogger log = BootstrapLogger.getLogger(TrafficHunterAgentStartAction.class);

    private static final Boolean bannerMode = Configurations.banner(ConfigProperty.BANNER_MODE);

    private final TrafficHunterAgentShutdownHook shutdownHook;

    private final AsciiBanner banner = new AsciiBanner();

    private final StartUp startUp = new StartUp();

    public TrafficHunterAgentStartAction(final TrafficHunterAgentShutdownHook shutdownHook) {
        this.shutdownHook = shutdownHook;
    }

    /**
     * Initializes and executes the TrafficHunter Agent.
     * <p>This method performs the following tasks:</p>
     * <ul>
     *     <li>Loads environment-specific configurations using {@link YamlConfigurableEnvironment}.</li>
     *     <li>Initializes the agent's execution context and metadata.</li>
     *     <li>Registers shutdown hooks for cleanup during termination.</li>
     *     <li>Starts the agent's core logic in a dedicated thread.</li>
     * </ul>
     */
    @Override
    public void start(final Instrumentation inst, final String envPath) {

        final Instant startTime = startUp.getStartTime();

        banner.print(bannerMode);

        ConfigurableEnvironment environment = new YamlConfigurableEnvironment(envPath);
        AgentExecutableContext context = new TrafficHunterAgentExecutableContext(environment, shutdownHook);
        ConfigurableContextInitializer configurableContextInitializer = context.init();
        TrafficHunterAgentProperty property = configurableContextInitializer.property();
        AgentMetadata metadata = configurableContextInitializer.setAgentMetadata(
                startTime,
                AgentStatus.INITIALIZED
        );
        context.addAgentStateEventListener(metadata);
        configurableContextInitializer.retransform(inst);

        AgentRunner runner = new AgentRunner(property, context, metadata);
        Thread runnerThread = new Thread(runner);
        runnerThread.setUncaughtExceptionHandler(registertUncaughtExceptionHandler());
        runnerThread.setName(setRunnerThreadName());

        if(context.isInit()) {
            shutdownHook.addRuntimeShutdownHook(runner::close);
            runnerThread.start();
            context.close();
        }

        log.info("Started TrafficHunter Agent in {} second", startUp.getUpTime());
    }

    @Override
    public ClassLoader getAgentStartClassLoader() {
        return TrafficHunterAgentStartAction.class.getClassLoader();
    }

    /**
     * The {@code StartUp} class extends {@link LifeCycle} to measure the agent's
     * startup durations.
     *
     * <p>Features:</p>
     * <ul>
     *     <li>Tracks the agent's start time and end time.</li>
     *     <li>Calculates the total uptime.</li>
     * </ul>
     */
    private static class StartUp extends LifeCycle {

        public StartUp() {
            super();
        }

        @Override
        public Instant getStartTime() {
            return this.startTime;
        }

        @Override
        public Instant getEndTime() {
            if(endTime == null) {
                this.endTime = Instant.now();
            }
            return endTime;
        }

        @Override
        public Double getUpTime() {
            if(getStartTime() == null && getEndTime() == null) {
                throw new IllegalStateException("No start time or end time specified");
            }

            return Duration.between(getStartTime(), getEndTime()).toMillis() / 1_000.0;
        }
    }

    private String setRunnerThreadName() {
        return "TrafficHunterAgentRunnerThread";
    }

    /**
     * The {@code AgentRunner} class implements {@link Runnable} to execute the core logic
     * of the TrafficHunter Agent in a separate thread. It initializes the session manager
     * and orchestrates agent operations after the target application is loaded.
     *
     * <p>Features:</p>
     * <ul>
     *     <li>Introduces a delay to ensure the target application is fully loaded before execution.</li>
     * </ul>
     */
    private static final class AgentRunner implements Runnable, Closeable {

        private final TrafficHunterWebsocketClient client;

        private final AgentExecutableContext context;

        private final OpenTelemetrySdk openTelemetrySdk;

        private final ScheduledExecutorService schedule;

        private final TrafficHunterAgentProperty property;

        private final AgentMetadata metadata;

        private final JmxMetricSender jmxMetricSender;

        public AgentRunner(final TrafficHunterAgentProperty property,
                           final AgentExecutableContext context,
                           final AgentMetadata metadata) {

            this.client = initializeSender(property, metadata);
            this.openTelemetrySdk = initializeOpenTelemetry(metadata.agentName(), client, metadata);
            this.context = context;
            this.schedule = Executors.newSingleThreadScheduledExecutor();
            this.property = property;
            this.jmxMetricSender = new JmxMetricSender(property.targetUri());
            this.metadata = metadata;
        }

        /**
         * Executes the agent's core logic. Introduces a delay to ensure that
         * the target application is fully loaded before starting.
         */
        @Override
        public void run() {
            log.info("start agent sender!!");
            context.setStatus(AgentStatus.RUNNING);

            if(!shouldStart()) {
                throw new IllegalStateException("Agent has not been started yet");
            }

            schedule.scheduleWithFixedDelay(() -> {
                        SystemInfo systemInfo = jmxMetricSender.collect();
                        MetadataWrapper<SystemInfo> metadataWrapper = MetadataWrapper.create(metadata, systemInfo);
                        client.toSend(metadataWrapper, MetricType.SYSTEM_METRIC);
                    },
                    0,
                    property.scheduleInterval(),
                    property.timeUnit()
            );
        }

        @Override
        public void close() {
            openTelemetrySdk.close();
            schedule.shutdown();
            client.close();
        }

        private boolean shouldStart() {
            return context.isRunning();
        }

        private TrafficHunterWebsocketClient initializeSender(final TrafficHunterAgentProperty property,
                                                              final AgentMetadata metadata) {
            return TrafficHunterWebsocketClient.builder()
                    .endpoint(AgentUtil.WEBSOCKET_URL.getUri(property.serverUri()))
                    .maxAttempts(property.maxAttempt())
                    .backOffPolicy(property.backOffPolicy())
                    .metadata(getMetadata(metadata))
                    .build();
        }

        private Metadata getMetadata(final AgentMetadata metadata) {
            return Metadata.builder()
                    .agentId(metadata.agentId())
                    .agentVersion(metadata.agentVersion())
                    .agentName(metadata.agentName())
                    .startTime(metadata.startTime())
                    .status(metadata.status().get())
                    .build();
        }

        private OpenTelemetrySdk initializeOpenTelemetry(final String serviceName,
                                                         final TrafficHunterWebsocketClient client,
                                                         final AgentMetadata metadata) {

            OpenTelemetrySdk openTelemetrySdk = OpenTelemetryManager.manageOpenTelemetrySdk(
                    serviceName,
                    client,
                    metadata
            );

            OpenTelemetrySdkBridge.setOpenTelemetrySdkForceFlush((timeout, unit) -> {
                openTelemetrySdk.getSdkTracerProvider().forceFlush().join(timeout, unit);
                openTelemetrySdk.getSdkMeterProvider().forceFlush().join(timeout, unit);
                openTelemetrySdk.getSdkLoggerProvider().forceFlush().join(timeout, unit);
            });

            return openTelemetrySdk;
        }
    }

    private UncaughtExceptionHandler registertUncaughtExceptionHandler() {
        return (uncaughtException, throwable) ->
                log.info("Unhandled exception in {} : {}", uncaughtException.getName(), throwable.getMessage());
    }
}
