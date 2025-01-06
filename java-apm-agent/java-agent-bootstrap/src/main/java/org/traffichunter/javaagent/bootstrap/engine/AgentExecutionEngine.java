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
package org.traffichunter.javaagent.bootstrap.engine;

import java.lang.instrument.Instrumentation;
import java.time.Duration;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.traffichunter.javaagent.bootstrap.banner.AsciiBanner;
import org.traffichunter.javaagent.bootstrap.engine.context.AgentExecutableContext;
import org.traffichunter.javaagent.bootstrap.engine.context.configuration.ConfigurableContextInitializer;
import org.traffichunter.javaagent.bootstrap.engine.context.execute.TrafficHunterAgentExecutableContext;
import org.traffichunter.javaagent.bootstrap.engine.env.ConfigurableEnvironment;
import org.traffichunter.javaagent.bootstrap.engine.env.yaml.YamlConfigurableEnvironment;
import org.traffichunter.javaagent.bootstrap.engine.lifecycle.LifeCycle;
import org.traffichunter.javaagent.bootstrap.engine.property.TrafficHunterAgentProperty;
import org.traffichunter.javaagent.bootstrap.engine.sender.manager.MetricSendSessionManager;
import org.traffichunter.javaagent.bootstrap.metadata.AgentMetadata;
import org.traffichunter.javaagent.commons.status.AgentStatus;
import org.traffichunter.javaagent.trace.exporter.TraceExporter;
import org.traffichunter.javaagent.trace.manager.TraceManager;
import org.traffichunter.javaagent.trace.queue.TraceQueue;

/**
 * <p>
 *  The {@code AgentExecutionEngine} class is the core execution engine responsible for
 *  initializing, configuring, running, and shutting down the TrafficHunter Agent.
 *  This class encapsulates all the necessary steps to bootstrap the agent, manage its
 *  lifecycle, and ensure proper cleanup upon application termination.
 * </p>
 * <h4>Purpose:</h4>
 * <ul>
 *     <li>Initialize and configure the TrafficHunter Agent with environment-specific settings.</li>
 *     <li>Run the agent in a dedicated thread to monitor and manage traffic.</li>
 *     <li>Register and manage shutdown hooks to ensure proper cleanup during termination.</li>
 * </ul>
 *
 * <h4>Key Components:</h4>
 * <ul>
 *     <li>{@link TrafficHunterAgentShutdownHook} - Manages graceful shutdown of resources.</li>
 *     <li>{@link ConfigurableEnvironment} - Loads environment-specific configurations.</li>
 *     <li>{@link AgentRunner} - Executes the agent's core logic in a separate thread.</li>
 * </ul>
 *
 * <h4>Limitations:</h4>
 * <ul>
 *     <li>This class assumes that the {@code Instrumentation} object is correctly provided at runtime.</li>
 *     <li>Agent initialization failures may result in partial cleanup if not handled carefully.</li>
 * </ul>
 *
 * @see TrafficHunterAgentShutdownHook
 * @see AgentRunner
 * @see ConfigurableEnvironment
 * @see Instrumentation
 * @author yungwang-o
 * @version 1.0.0
 */
public final class AgentExecutionEngine {

    private static final Logger log = LoggerFactory.getLogger(AgentExecutionEngine.class);

    private final TrafficHunterAgentShutdownHook shutdownHook = new TrafficHunterAgentShutdownHook();

    private final AsciiBanner asciiBanner = new AsciiBanner();

    private final ConfigurableEnvironment environment;

    private final Instrumentation inst;

    private AgentExecutionEngine(final String args, final Instrumentation inst) {
        this.inst = inst;
        this.environment = new YamlConfigurableEnvironment(args);
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
    private void run() {
        StartUp startUp = new StartUp();
        Instant startTime = startUp.getStartTime();
        final AgentExecutableContext context = new TrafficHunterAgentExecutableContext(environment, shutdownHook);
        if(!shutdownHook.isEnabledShutdownHook()) {
            shutdownHook.enableShutdownHook();
        }
        ConfigurableContextInitializer configurableContextInitializer = context.init();
        TraceManager traceManager = configurableContextInitializer.setTraceManager(new TraceExporter());
        configurableContextInitializer.retransform(inst);
        TrafficHunterAgentProperty property = configurableContextInitializer.property();
        AgentMetadata metadata = configurableContextInitializer.setAgentMetadata(
                startTime,
                AgentStatus.INITIALIZED
        );
        context.addAgentStateEventListener(metadata);
        asciiBanner.print(metadata);
        AgentRunner runner = new AgentRunner(property, context, metadata);
        Thread runnerThread = new Thread(runner);
        runnerThread.setName("TrafficHunterAgentRunnerThread");
        if(context.isInit()) {
            log.info("Agent initialization completed.");
            runnerThread.start();
            registryShutdownHook(context, runner, traceManager);
            context.close();
        }

        log.info("Started TrafficHunter Agent in {} second", String.format("%.3f", startUp.getUpTime()));
    }

    /**
     * Registers shutdown hooks for the agent's cleanup operations.
     *
     * @param context The execution context of the agent.
     * @param runner  The agent runner instance responsible for managing execution.
     * @param traceManager The trace manager manages a trace's lifecycle.
     */
    private void registryShutdownHook(final AgentExecutableContext context,
                                      final AgentRunner runner,
                                      final TraceManager traceManager) {

        shutdownHook.addRuntimeShutdownHook(traceManager::close);
        shutdownHook.addRuntimeShutdownHook(TraceQueue.INSTANCE::removeAll);
        shutdownHook.addRuntimeShutdownHook(context::removeAllAgentStateEventListeners);
        shutdownHook.addRuntimeShutdownHook(runner::close);
    }

    public static void run(final String args, final Instrumentation inst) {
        new AgentExecutionEngine(System.getProperty(args), inst).run();
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
