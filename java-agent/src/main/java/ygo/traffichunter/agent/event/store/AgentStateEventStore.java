package ygo.traffichunter.agent.event.store;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import ygo.traffichunter.agent.event.listener.AgentStateEventListener;

public class AgentStateEventStore {

    private final List<AgentStateEventListener> listeners = new CopyOnWriteArrayList<>();

    protected void addAgentStateEventListener(final AgentStateEventListener listener) {
        listeners.add(listener);
    }

    protected void removeAgentStateEventListener(final AgentStateEventListener listener) {
        if(listeners.isEmpty()) {
            throw new IllegalArgumentException("listener is empty");
        }

        listeners.remove(listener);
    }

    public List<AgentStateEventListener> getListeners() {
        return listeners;
    }

    protected void removeAll() {
        listeners.clear();
    }

    public int listenerSize() {
        return listeners.size();
    }
}
