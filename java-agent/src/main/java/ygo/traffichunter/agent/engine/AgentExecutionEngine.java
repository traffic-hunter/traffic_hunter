package ygo.traffichunter.agent.engine;

import java.lang.instrument.Instrumentation;
import java.time.Duration;
import java.time.Instant;
import ygo.traffichunter.agent.AgentStatus;
import ygo.traffichunter.agent.banner.AsciiBanner;
import ygo.traffichunter.agent.engine.context.AgentExecutableContext;
import ygo.traffichunter.agent.engine.context.configuration.ConfigurableContextInitializer;
import ygo.traffichunter.agent.engine.context.execute.TrafficHunterAgentExecutableContext;
import ygo.traffichunter.agent.engine.env.ConfigurableEnvironment;
import ygo.traffichunter.agent.engine.env.yaml.YamlConfigurableEnvironment;
import ygo.traffichunter.agent.engine.lifecycle.LifeCycle;
import ygo.traffichunter.agent.engine.sender.manager.MetricSendSessionManager;
import ygo.traffichunter.agent.engine.systeminfo.metadata.AgentMetadata;
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

    private static final TrafficHunterAgentShutdownHook shutdownHook = new TrafficHunterAgentShutdownHook();

    private final AsciiBanner asciiBanner = new AsciiBanner();

    private final ConfigurableEnvironment environment;

    private final Instrumentation inst;

    private AgentExecutionEngine(final String args, final Instrumentation inst) {
        this.inst = inst;
        this.environment = new YamlConfigurableEnvironment(args);
    }

    private void run() {
        StartUp startUp = new StartUp();
        final AgentExecutableContext context = new TrafficHunterAgentExecutableContext(environment, shutdownHook);
        if(!shutdownHook.isEnabledShutdownHook()) {
            shutdownHook.enableShutdownHook();
        }
        ConfigurableContextInitializer configurableContextInitializer = context.init();
        configurableContextInitializer.retransform(inst);
        TrafficHunterAgentProperty property = configurableContextInitializer.property();
        AgentMetadata metadata = configurableContextInitializer.setAgentMetadata(
                startUp.getStartTime(),
                AgentStatus.INITIALIZED
        );
        context.addAgentStateEventListener(metadata);
        asciiBanner.print(metadata);
        AgentRunner runner = new AgentRunner(property, context, metadata);
        runner.run();
        shutdownHook.addRuntimeShutdownHook(runner::close);
        context.close();
    }

    public static void run(final String args, final Instrumentation inst) {
        new AgentExecutionEngine(System.getProperty(args), inst).run();
    }

    /**
     * agent's start and end time measure
     */
    static class StartUp extends LifeCycle {

        @Override
        public Instant getStartTime() {
            return this.startTime;
        }

        @Override
        public Instant getEndTime() {
            this.endTime = Instant.now();
            return endTime;
        }

        @Override
        public Duration getUpTime() {
            return Duration.between(startTime, endTime);
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

        public void run() {
            sessionManager.run();
        }

        public void close() {
            sessionManager.close();
        }
    }
}
