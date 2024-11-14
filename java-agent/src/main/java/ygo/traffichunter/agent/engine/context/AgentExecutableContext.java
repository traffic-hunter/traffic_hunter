package ygo.traffichunter.agent.engine.context;

import ygo.traffichunter.agent.AgentStatus;
import ygo.traffichunter.agent.engine.context.configuration.ConfigurableContextInitializer;
import ygo.traffichunter.agent.engine.env.ConfigurableEnvironment;
import ygo.traffichunter.agent.event.context.AgentContextStateEventHandler;

public interface AgentExecutableContext extends AgentContextStateEventHandler {

    ConfigurableContextInitializer init();

    void close();

    ConfigurableEnvironment getEnvironment();

    AgentStatus getStatus();

    boolean isInit();

    boolean isRunning();

    boolean isStopped();

    void setStatus(AgentStatus status);
}
