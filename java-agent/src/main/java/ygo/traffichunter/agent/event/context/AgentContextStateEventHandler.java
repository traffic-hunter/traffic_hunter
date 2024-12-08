package ygo.traffichunter.agent.event.context;

import ygo.traffichunter.agent.event.listener.AgentStateEventListener;

/**
 * The {@code AgentContextStateEventHandler} interface defines methods for managing
 * {@link AgentStateEventListener} instances. It allows adding, removing, and clearing
 * listeners for handling agent state change events.
 *
 * @author yungwang-o
 * @version 1.0.0
 */
public interface AgentContextStateEventHandler {

    void addAgentStateEventListener(final AgentStateEventListener listener);

    void removeAgentStateEventListener(final AgentStateEventListener listener);

    void removeAllAgentStateEventListeners();
}
