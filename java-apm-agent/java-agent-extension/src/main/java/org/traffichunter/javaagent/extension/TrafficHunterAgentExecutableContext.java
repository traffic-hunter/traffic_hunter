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
package org.traffichunter.javaagent.extension;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;
import org.traffichunter.javaagent.bootstrap.TrafficHunterAgentShutdownHook;
import org.traffichunter.javaagent.commons.status.AgentStatus;
import org.traffichunter.javaagent.event.listener.AgentStateEventListener;
import org.traffichunter.javaagent.event.object.AgentStateEvent;
import org.traffichunter.javaagent.event.store.AgentStateEventStore;
import org.traffichunter.javaagent.extension.env.ConfigurableEnvironment;

/**
 * <p>
 * The {@code TrafficHunterAgentExecutableContext} class represents the execution context
 * for the TrafficHunter Agent. It manages the agent's state, event listeners, environment
 * configuration, and shutdown operations.
 * </p>
 *
 * <p>Purpose:</p>
 * <ul>
 *     <li>Maintains the current state of the agent using an atomic {@link AgentStatus}.</li>
 *     <li>Registers and manages {@link AgentStateEventListener} instances for state change notifications.</li>
 *     <li>Integrates a shutdown mechanism using {@link TrafficHunterAgentShutdownHook}.</li>
 * </ul>
 *
 * <p>Key Features:</p>
 * <ul>
 *     <li>{@code init()} - Initializes the agent with the provided environment settings.</li>
 *     <li>{@code addAgentStateEventListener()} - Adds a listener to observe state changes.</li>
 *     <li>{@code removeAllAgentStateEventListeners()} - Removes all registered listeners.</li>
 *     <li>{@code close()} - Safely shuts down the agent using a dedicated shutdown thread.</li>
 *     <li>{@code setStatus()} - Atomically updates the agent's state and notifies listeners of the change.</li>
 * </ul>
 *
 * <p>Thread Safety:</p>
 * <ul>
 *     <li>The {@code status} is managed using {@link AtomicReference}, ensuring thread-safe updates.</li>
 *     <li>Shutdown operations are protected by a {@link ReentrantLock} to prevent concurrent execution.</li>
 *     <li>{@link AtomicBoolean} is used to ensure the shutdown logic executes only once.</li>
 * </ul>
 *
 * <p>Limitations:</p>
 * <ul>
 *     <li>State listeners are invoked sequentially; long-running listeners may delay others.</li>
 *     <li>Shutdown operations rely on an additional thread, which may cause delays during JVM termination.</li>
 * </ul>
 *
 * @see AgentExecutableContext
 * @see AgentStatus
 * @see TrafficHunterAgentShutdownHook
 * @see ConfigurableContextInitializer
 * @see AgentStateEventListener
 * @author yungwang-o
 * @version 1.0.0
 */
final class TrafficHunterAgentExecutableContext extends AgentStateEventStore implements AgentExecutableContext {

    private static final Logger log = Logger.getLogger(TrafficHunterAgentExecutableContext.class.getName());

    private final ConfigurableEnvironment environment;

    private final AtomicReference<AgentStatus> status = new AtomicReference<>(AgentStatus.INITIALIZED);

    private final TrafficHunterAgentShutdownHook shutdownHook;

    private final ReentrantLock shutdownLock = new ReentrantLock();

    private boolean isShutdown = false;

    TrafficHunterAgentExecutableContext(final ConfigurableEnvironment environment,
                                        final TrafficHunterAgentShutdownHook shutdownHook) {
        this.environment = environment;
        this.shutdownHook = shutdownHook;
    }

    @Override
    public void addAgentStateEventListener(final AgentStateEventListener listener) {
        super.addAgentStateEventListener(listener);
    }

    @Override
    public void removeAgentStateEventListener(final AgentStateEventListener listener) {
        super.removeAgentStateEventListener(listener);
    }

    @Override
    public void removeAllAgentStateEventListeners() {
        super.removeAll();
    }

    @Override
    public ConfigurableContextInitializer init() {
        return new ConfigurableContextInitializer(environment);
    }

    /**
     * Safely shuts down the agent by executing the registered shutdown hooks.
     * <p>Ensures that:</p>
     * <ul>
     *     <li>The shutdown hook is executed only once.</li>
     *     <li>Concurrent shutdown attempts are prevented using a {@link ReentrantLock}.</li>
     * </ul>
     * If the shutdown hook is not enabled, this method does nothing.
     */
    @Override
    public void close() {

        shutdownLock.lock();

        try {
            if (isStopped() && !isShutdown) {
                return;
            }

            if (this.shutdownHook.isEnabledShutdownHook()) {
                Thread shutdownHookThread = new Thread(this.shutdownHook, "TrafficHunterAgentShutdownHook");
                shutdownHookThread.setUncaughtExceptionHandler(registerThreadExceptionHandler());
                shutdownHookThread.start();
                isShutdown = true;
            }
        } finally {
            shutdownLock.unlock();
        }
    }

    @Override
    public ConfigurableEnvironment getEnvironment() {
        return this.environment;
    }

    @Override
    public AgentStatus getStatus() {
        return status.get();
    }

    @Override
    public boolean isInit() {
        return status.get() == AgentStatus.INITIALIZED;
    }

    @Override
    public boolean isRunning() {
        return status.get() == AgentStatus.RUNNING;
    }

    @Override
    public boolean isStopped() {
        return status.get() == AgentStatus.EXIT;
    }

    @Override
    public void setStatus(final AgentStatus newStatus) {
        AgentStatus agentStatus;

        do {
            agentStatus = status.get();
        } while (!status.compareAndSet(agentStatus, newStatus));

        AgentStateEvent event = new AgentStateEvent(this, agentStatus, newStatus);

        notifyAgentStateChange(event);
    }

    private void notifyAgentStateChange(final AgentStateEvent event) {
        for(AgentStateEventListener listener : super.getListeners()) {
            listener.onEvent(event);
        }
    }

    private UncaughtExceptionHandler registerThreadExceptionHandler() {
        return (thread, throwable) ->
                log.warning("Unhandled exception in " + thread.getName() + " : " + throwable.getMessage());
    }
}
