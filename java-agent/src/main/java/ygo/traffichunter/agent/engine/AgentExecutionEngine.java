package ygo.traffichunter.agent.engine;

import java.lang.instrument.Instrumentation;
import java.time.Duration;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ygo.traffichunter.agent.AgentStatus;
import ygo.traffichunter.agent.banner.AsciiBanner;
import ygo.traffichunter.agent.engine.context.AgentExecutableContext;
import ygo.traffichunter.agent.engine.context.configuration.ConfigurableContextInitializer;
import ygo.traffichunter.agent.engine.context.execute.TrafficHunterAgentExecutableContext;
import ygo.traffichunter.agent.engine.env.ConfigurableEnvironment;
import ygo.traffichunter.agent.engine.env.yaml.YamlConfigurableEnvironment;
import ygo.traffichunter.agent.engine.lifecycle.LifeCycle;
import ygo.traffichunter.agent.engine.queue.SyncQueue;
import ygo.traffichunter.agent.engine.sender.manager.MetricSendSessionManager;
import ygo.traffichunter.agent.engine.metric.metadata.AgentMetadata;
import ygo.traffichunter.agent.property.TrafficHunterAgentProperty;

/**
 * The {@code AgentExecutionEngine} class is the core execution engine responsible for
 * initializing, configuring, running, and shutting down the TrafficHunter Agent.
 * This class encapsulates all the necessary steps to bootstrap the agent, manage its
 * lifecycle, and ensure proper cleanup upon application termination.
 *
 * <p>Purpose:</p>
 * <ul>
 *     <li>Initialize and configure the TrafficHunter Agent with environment-specific settings.</li>
 *     <li>Run the agent in a dedicated thread to monitor and manage traffic.</li>
 *     <li>Register and manage shutdown hooks to ensure proper cleanup during termination.</li>
 * </ul>
 *
 * <p>Key Components:</p>
 * <ul>
 *     <li>{@link TrafficHunterAgentShutdownHook} - Manages graceful shutdown of resources.</li>
 *     <li>{@link ConfigurableEnvironment} - Loads environment-specific configurations.</li>
 *     <li>{@link AgentRunner} - Executes the agent's core logic in a separate thread.</li>
 *     <li>{@link AsciiBanner} - Prints metadata and banner information to the console.</li>
 * </ul>
 *
 * <p>Thread Safety:</p>
 * <ul>
 *     <li>The class uses thread-safe constructs such as {@link TrafficHunterAgentShutdownHook}
 *         to manage concurrent operations.</li>
 *     <li>Execution and shutdown are handled in separate threads to prevent blocking the main process.</li>
 * </ul>
 *
 * <p>Limitations:</p>
 * <ul>
 *     <li>This class assumes that the {@code Instrumentation} object is correctly provided at runtime.</li>
 *     <li>Agent initialization failures may result in partial cleanup if not handled carefully.</li>
 * </ul>
 *
 * @see TrafficHunterAgentShutdownHook
 * @see AgentExecutionEngine.AgentRunner
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
            registryShutdownHook(context, runner);
            context.close();
        }

        log.info("Started TrafficHunter Agent in {} second", startUp.getUpTime().getSeconds() / 1000);
    }

    /**
     * Registers shutdown hooks for the agent's cleanup operations.
     * <p>Ensures that all resources are released properly, including:</p>
     * <ul>
     *     <li>Removing all state from {@link SyncQueue}.</li>
     *     <li>Clearing agent state event listeners from the context.</li>
     *     <li>Closing the {@link AgentRunner} and releasing associated resources.</li>
     * </ul>
     *
     * @param context The execution context of the agent.
     * @param runner  The agent runner instance responsible for managing execution.
     */
    private void registryShutdownHook(final AgentExecutableContext context, final AgentRunner runner) {
        shutdownHook.addRuntimeShutdownHook(SyncQueue.INSTANCE::removeAll);
        shutdownHook.addRuntimeShutdownHook(context::removeAllAgentStateEventListeners);
        shutdownHook.addRuntimeShutdownHook(runner::close);
    }

    public static void run(final String args, final Instrumentation inst) {
        new AgentExecutionEngine(System.getProperty(args), inst).run();
    }

    /**
     * The {@code StartUp} class extends {@link LifeCycle} to measure the agent's
     * startup and shutdown durations. It provides timestamps for the agent's
     * lifecycle events and calculates the total uptime.
     *
     * <p>Features:</p>
     * <ul>
     *     <li>Tracks the agent's start time and end time.</li>
     *     <li>Calculates the total uptime using {@link Duration}.</li>
     * </ul>
     */
    static class StartUp extends LifeCycle {

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
        public Duration getUpTime() {
            return Duration.between(this.startTime, getEndTime());
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
    static final class AgentRunner implements Runnable {

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
                // run after loading the target application
                log.info("Waiting for Agent Runner...");
                Thread.sleep(5000);
                sessionManager.run();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
        }

        public void close() {
            sessionManager.close();
        }
    }
}
