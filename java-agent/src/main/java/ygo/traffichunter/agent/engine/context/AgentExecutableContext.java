package ygo.traffichunter.agent.engine.context;

import ygo.traffichunter.agent.AgentStatus;
import ygo.traffichunter.agent.engine.context.configuration.ConfigurableContextInitializer;
import ygo.traffichunter.agent.engine.env.ConfigurableEnvironment;
import ygo.traffichunter.agent.event.context.AgentContextStateEventHandler;

/**
 * The {@code AgentExecutableContext} interface defines the contract for managing
 * the lifecycle of an agent's execution context. It includes methods for initializing
 * the context, managing its state, and handling lifecycle events.
 *
 * <p>Features:</p>
 * <ul>
 *     <li>Extends {@link AgentContextStateEventHandler} for state event listener management.</li>
 *     <li>Provides methods to initialize, close, and query the context's status.</li>
 *     <li>Supports dynamic updates to the agent's execution status.</li>
 * </ul>
 *
 * @see AgentContextStateEventHandler
 * @see ConfigurableContextInitializer
 * @see ConfigurableEnvironment
 * @see AgentStatus
 *
 * @author yungwang-o
 * @version 1.0.0
 */
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
