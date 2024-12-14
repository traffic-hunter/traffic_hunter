/**
 * The MIT License
 *
 * Copyright (c) 2024 yungwang-o
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
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
