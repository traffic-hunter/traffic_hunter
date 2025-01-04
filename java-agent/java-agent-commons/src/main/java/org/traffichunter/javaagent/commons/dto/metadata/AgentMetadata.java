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
package org.traffichunter.javaagent.commons.dto.metadata;

import java.time.Instant;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import org.traffichunter.javaagent.commons.status.AgentStatus;
import org.traffichunter.javaagent.event.listener.AgentStateEventListener;
import org.traffichunter.javaagent.event.object.AgentStateEvent;

/**
 * The {@code AgentMetadata} record represents metadata for an agent and acts as an
 * {@link AgentStateEventListener} to respond to state change events.
 *
 * <p>Features:</p>
 * <ul>
 *     <li>Stores immutable metadata fields such as agent ID, version, name, and start time.</li>
 *     <li>Maintains a mutable {@link AtomicReference} for the agent's current status.</li>
 *     <li>Implements {@link AgentStateEventListener} to react to state change events.</li>
 *     <li>Overrides {@code equals} and {@code hashCode} to handle mutable status appropriately.</li>
 * </ul>
 *
 * @see AgentStateEventListener
 * @see AgentStateEvent
 * @see AgentStatus
 * @see AtomicReference
 *
 * @author yungwang-o
 * @version 1.0.0
 */
public record AgentMetadata(
        String agentId,
        String agentVersion,
        String agentName,
        Instant startTime,
        AtomicReference<AgentStatus> status
) implements AgentStateEventListener {

    @Override
    public void onEvent(final AgentStateEvent event) {
        this.setStatus(event.getAfterStatus());
    }

    private void setStatus(final AgentStatus status) {
        this.status.set(status);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AgentMetadata that = (AgentMetadata) o;
        return Objects.equals(agentId, that.agentId) &&
                Objects.equals(agentVersion, that.agentVersion) &&
                Objects.equals(agentName, that.agentName) &&
                Objects.equals(startTime, that.startTime) &&
                Objects.equals(status.get(), that.status.get());
    }

    @Override
    public int hashCode() {
        return Objects.hash(agentId, agentVersion, agentName, startTime, status.get());
    }
}
