package ygo.traffichunter.agent.event.context;

import ygo.traffichunter.agent.event.listener.AgentStateEventListener;

public interface AgentContextStateEventHandler {

    void addAgentStateEventListener(final AgentStateEventListener listener);

    void removeAgentStateEventListener(final AgentStateEventListener listener);

    void removeAllAgentStateEventListeners();
}
