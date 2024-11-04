package ygo.traffichunter.agent.engine;

import java.time.Duration;
import java.time.Instant;
import java.util.function.Supplier;
import ygo.traffichunter.agent.AgentStatus;
import ygo.traffichunter.agent.banner.AsciiBanner;
import ygo.traffichunter.agent.engine.context.AgentExecutableContext;
import ygo.traffichunter.agent.engine.context.configuration.ConfigurableContextInitializer;
import ygo.traffichunter.agent.engine.context.execute.TrafficHunterAgentExecutableContext;
import ygo.traffichunter.agent.engine.env.ConfigurableEnvironment;
import ygo.traffichunter.agent.engine.env.Environment;
import ygo.traffichunter.agent.engine.env.yaml.YamlConfigurableEnvironment;
import ygo.traffichunter.agent.engine.lifecycle.LifeCycle;
import ygo.traffichunter.agent.engine.sender.manager.MetricSendSessionManager;
import ygo.traffichunter.agent.engine.systeminfo.TransactionInfo;
import ygo.traffichunter.agent.engine.systeminfo.metadata.AgentMetadata;
import ygo.traffichunter.agent.property.TrafficHunterAgentProperty;
import ygo.traffichunter.event.handler.TrafficHunterEventHandler;

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

    private final TrafficHunterEventHandler<TransactionInfo> eventHandler = new TrafficHunterEventHandler<>();

    private final AsciiBanner asciiBanner = new AsciiBanner();

    private final ConfigurableEnvironment environment;

    private AgentExecutionEngine(final String[] args) {
        this.environment = new YamlConfigurableEnvironment(findEnvPath(args));
    }

    private void run() {

        StartUp startUp = new StartUp();
        AgentExecutableContext context = new TrafficHunterAgentExecutableContext(environment);
        shutdownHook.enableShutdownHook();
        ConfigurableContextInitializer configurableContextInitializer = context.configureEnv();
        TrafficHunterAgentProperty property = configurableContextInitializer.property();
        configurableContextInitializer.attach(property);
        AgentMetadata metadata = configurableContextInitializer.getAgentMetadata();
        metadata.setStartTime(startUp.getStartTime());
        asciiBanner.print(metadata);
        AgentRunner runner = new AgentRunner(property, context);
        try {
            runner.run();
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }

    }

    public static void run(final String[] args) {
        new AgentExecutionEngine(args).run();
    }

    private static class StartUp extends LifeCycle {

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

    private static class AgentRunner {

        private final MetricSendSessionManager sessionManager;

        private final AgentExecutableContext context;

        public AgentRunner(final TrafficHunterAgentProperty property, final AgentExecutableContext context) {
            this.context = context;
            this.sessionManager = new MetricSendSessionManager(property, context);
        }

        public void run() {
            sessionManager.start();
        }
    }

    private String findEnvPath(String[] args) {

        for(String arg : args) {
            if(arg.contains("--config")) {
                return arg.substring(9);
            }
        }

        throw new RuntimeException("No config found!!");
    }
}
