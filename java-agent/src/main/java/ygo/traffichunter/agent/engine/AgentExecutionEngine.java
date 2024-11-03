package ygo.traffichunter.agent.engine;

import java.time.Instant;
import ygo.traffichunter.agent.banner.AsciiBanner;
import ygo.traffichunter.agent.engine.context.AgentExecutableContext;
import ygo.traffichunter.agent.engine.context.configuration.ConfigurableContextInitializer;
import ygo.traffichunter.agent.engine.context.execute.TrafficHunterAgentExecutableContext;
import ygo.traffichunter.agent.engine.env.Environment;
import ygo.traffichunter.agent.engine.env.yaml.YamlConfigurableEnvironment;
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

    private final MetricSendSessionManager sessionManager;

    private static final TrafficHunterEventHandler<TransactionInfo> eventHandler = new TrafficHunterEventHandler<>();

    private final AgentExecutableContext context;

    private final AsciiBanner asciiBanner = new AsciiBanner();

    private AgentExecutionEngine(final TrafficHunterAgentProperty property) {

        this.sessionManager = new MetricSendSessionManager(property);

        this.context = new TrafficHunterAgentExecutableContext(
                new YamlConfigurableEnvironment(),
                this.sessionManager
        );
    }

    private void run() {

        StartUp startUp = StartUp.create();
        ConfigurableContextInitializer configurableContextInitializer = context.configureEnv();
        TrafficHunterAgentProperty initialize = configurableContextInitializer.initialize(Environment.DEFAULT_PATH.path());
        configurableContextInitializer.attach(initialize);
        AgentMetadata metadata = configurableContextInitializer.getAgentMetadata(Environment.DEFAULT_PATH.path());
        asciiBanner.print(metadata);



        sessionManager.start();

        shutdown();
    }

    public static void run(final TrafficHunterAgentProperty property) {
        new AgentExecutionEngine(property).run();
    }

    private void shutdown() {
        shutdownHook.addRuntimeShutdownHook(eventHandler::close);
        shutdownHook.addRuntimeShutdownHook(sessionManager::close);
    }

    private record StartUp(long startTime) {

        private static StartUp create() {
            return new StartUp(Instant.now().toEpochMilli());
        }

        private long startupTime() {
            return this.startTime;
        }

        private long endTime() {
            return Instant.now().toEpochMilli() - startTime;
        }
    }
}
