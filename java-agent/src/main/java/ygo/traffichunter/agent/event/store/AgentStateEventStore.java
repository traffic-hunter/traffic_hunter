package ygo.traffichunter.agent.event.store;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import ygo.traffichunter.agent.event.listener.AgentStateEventListener;

/**
 * The {@code AgentStateEventStore} class manages a collection of {@link AgentStateEventListener} instances.
 * It provides methods to add, remove, and retrieve listeners, enabling event-driven state management
 * for the TrafficHunter Agent.
 *
 * <p>Purpose:</p>
 * <ul>
 *     <li>Stores event listeners for agent state changes.</li>
 *     <li>Optimized for frequent reads and infrequent writes using {@link CopyOnWriteArrayList}.</li>
 * </ul>
 *
 * @see AgentStateEventListener
 * @see CopyOnWriteArrayList
 *
 * @author yungwang-o
 * @version 1.0.0
 */
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
