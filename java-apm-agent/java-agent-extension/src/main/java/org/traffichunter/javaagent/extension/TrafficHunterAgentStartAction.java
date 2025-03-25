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
import java.lang.instrument.Instrumentation;
import java.time.Instant;
import org.traffichunter.javaagent.bootstrap.BootstrapLogger;
import org.traffichunter.javaagent.bootstrap.OpenTelemetrySdkBridge;
import org.traffichunter.javaagent.bootstrap.TrafficHunterAgentShutdownHook;
import org.traffichunter.javaagent.bootstrap.TrafficHunterAgentStarter;
import org.traffichunter.javaagent.commons.status.AgentStatus;
import org.traffichunter.javaagent.extension.banner.AsciiBanner;
import org.traffichunter.javaagent.extension.banner.AsciiBanner.Mode;
import org.traffichunter.javaagent.extension.env.ConfigurableEnvironment;
import org.traffichunter.javaagent.extension.env.yaml.YamlConfigurableEnvironment;
import org.traffichunter.javaagent.extension.property.TrafficHunterAgentProperty;
import org.traffichunter.javaagent.extension.metadata.AgentMetadata;
import org.traffichunter.javaagent.extension.sender.manager.MetricSendSessionManager;

/**
 * @author yungwang-o
 * @version 1.1.0
 */
@SuppressWarnings("unused")
final class TrafficHunterAgentStartAction implements TrafficHunterAgentStarter {

    private static final BootstrapLogger log = BootstrapLogger.getLogger(TrafficHunterAgentStartAction.class);

    private final TrafficHunterAgentShutdownHook shutdownHook;

    private final AsciiBanner banner = new AsciiBanner();

    public TrafficHunterAgentStartAction(final TrafficHunterAgentShutdownHook shutdownHook) {
        this.shutdownHook = shutdownHook;
    }

    private static volatile Instrumentation INSTRUMENTATION;

    public static Instrumentation getInstrumentation() {
        return INSTRUMENTATION;
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
    public void start(final Instrumentation inst,
                      final String envPath,
                      final Instant startTime) {

        banner.print(Mode.ON);

        INSTRUMENTATION = inst;

        ConfigurableEnvironment environment = new YamlConfigurableEnvironment(envPath);
        AgentExecutableContext context = new TrafficHunterAgentExecutableContext(environment, shutdownHook);
        ConfigurableContextInitializer configurableContextInitializer = context.init();
        TrafficHunterAgentProperty property = configurableContextInitializer.property();
        AgentMetadata metadata = configurableContextInitializer.setAgentMetadata(
                startTime,
                AgentStatus.INITIALIZED
        );
        context.addAgentStateEventListener(metadata);
        OpenTelemetrySdk openTelemetrySdk = initializeOpenTelemetry();
        configurableContextInitializer.retransform(inst);

        AgentRunner runner = new AgentRunner(property, context, metadata);
        Thread runnerThread = new Thread(runner);
        runnerThread.setName(setRunnerThreadName());

        if(context.isInit()) {
            shutdownHook.addRuntimeShutdownHook(runner::close)
                    .addRuntimeShutdownHook(openTelemetrySdk::close);
            runnerThread.start();
            context.close();
        }
    }

    private OpenTelemetrySdk initializeOpenTelemetry() {

        OpenTelemetrySdk openTelemetrySdk = OpenTelemetryManager.manageOpenTelemetrySdk();

        OpenTelemetrySdkBridge.setOpenTelemetrySdkForceFlush((timeout, unit) -> {
                openTelemetrySdk.getSdkTracerProvider().forceFlush().join(timeout, unit);
                openTelemetrySdk.getSdkMeterProvider().forceFlush().join(timeout, unit);
                openTelemetrySdk.getSdkLoggerProvider().forceFlush().join(timeout, unit);
        });

        return openTelemetrySdk;
    }

    @Override
    public ClassLoader getAgentStartClassLoader() {
        return TrafficHunterAgentStartAction.class.getClassLoader();
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
     *     <li>Manages the lifecycle of the {@link MetricSendSessionManager}.</li>
     * </ul>
     */
    private static final class AgentRunner implements Runnable {

        private final MetricSendSessionManager sessionManager;

        public AgentRunner(final TrafficHunterAgentProperty property,
                           final AgentExecutableContext context,
                           final AgentMetadata metadata) {

            this.sessionManager = new MetricSendSessionManager(property, context, metadata);
        }

        /**
         * Executes the agent's core logic. Introduces a delay to ensure that
         * the target application is fully loaded before starting.
         */
        @Override
        public void run() {
            try {
                log.info("Waiting for Agent Runner...");
                Thread.sleep(8000);
                sessionManager.run();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        public void close() {
            sessionManager.close();
        }
    }
}
