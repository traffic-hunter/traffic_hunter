package ygo.traffichunter.agent.engine.context.execute;

import java.util.concurrent.atomic.AtomicReference;
import ygo.traffichunter.agent.AgentStatus;
import ygo.traffichunter.agent.engine.context.AgentExecutableContext;
import ygo.traffichunter.agent.engine.context.configuration.ConfigurableContextInitializer;
import ygo.traffichunter.agent.engine.env.ConfigurableEnvironment;

public class TrafficHunterAgentExecutableContext implements AgentExecutableContext {

    private final ConfigurableEnvironment environment;

    private final AtomicReference<AgentStatus> status = new AtomicReference<>();

    public TrafficHunterAgentExecutableContext(final ConfigurableEnvironment environment) {
        this.environment = environment;
    }

    @Override
    public ConfigurableContextInitializer configureEnv() {
        return new ConfigurableContextInitializer(environment);
    }

    @Override
    public void close() {

    }

    @Override
    public ConfigurableEnvironment getEnvironment() {
        return environment;
    }

    @Override
    public AgentStatus getStatus() {
        return status.get();
    }

    @Override
    public boolean isRunning() {
        return status.get().equals(AgentStatus.RUNNING);
    }

    @Override
    public boolean isStopped() {
        return status.get().equals(AgentStatus.EXIT);
    }

    @Override
    public boolean setStatus(final AgentStatus newStatus) {
        AgentStatus agentStatus = status.get();
        return status.compareAndSet(agentStatus, newStatus);
    }
}
