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
 * After selecting a JVM, this agent execution engine collects the metrics of the JVM at regular intervals and transmits them to the server.
 * <br/>
 * <br/>
 * The transmission method is HTTP.
 * <br/>
 * <br/>
 * what metric is it?
 * <br/>
 * <br/>
 * garbage collection (GC), thread, tomcat, memory heap, cpu usage, transaction
 * <br/>
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
        if(context.isInit()) {
            AgentRunner runner = new AgentRunner(property, context, metadata);
            runner.init();
            runner.run();
            registryShutdownHook(context, runner);
            context.close();
        }

        log.info("Started TrafficHunter Agent in {} second", startUp.getUpTime().getSeconds());
    }

    private void registryShutdownHook(final AgentExecutableContext context, final AgentRunner runner) {
        shutdownHook.addRuntimeShutdownHook(SyncQueue.INSTANCE::removeAll);
        shutdownHook.addRuntimeShutdownHook(context::removeAllAgentStateEventListeners);
        shutdownHook.addRuntimeShutdownHook(runner::close);
    }

    public static void run(final String args, final Instrumentation inst) {
        new AgentExecutionEngine(System.getProperty(args), inst).run();
    }

    /**
     * agent's start and end time measure
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
     * Responsible for executing the agent.
     */
    static final class AgentRunner {

        private final MetricSendSessionManager sessionManager;

        public AgentRunner(final TrafficHunterAgentProperty property,
                           final AgentExecutableContext context,
                           final AgentMetadata metadata) {

            this.sessionManager = new MetricSendSessionManager(property, context, metadata);
        }

        public void init() {
            sessionManager.afterConnectionEstablished();
        }

        public void run() {
            sessionManager.run();
        }

        public void close() {
            sessionManager.close();
        }
    }
}
