package ygo.traffichunter.agent.engine.context;

import ygo.traffichunter.agent.AgentStatus;
import ygo.traffichunter.agent.engine.context.configuration.ConfigurableContextInitializer;
import ygo.traffichunter.agent.engine.env.ConfigurableEnvironment;

public interface AgentExecutableContext {

    ConfigurableContextInitializer configureEnv();

    void close();

    ConfigurableEnvironment getEnvironment();

    AgentStatus getStatus();

    boolean isRunning();

    boolean isStopped();

    boolean setStatus(AgentStatus status);
}
