package ygo.traffichunter.agent.engine.context.execute;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;
import ygo.traffichunter.agent.AgentStatus;
import ygo.traffichunter.agent.engine.TrafficHunterAgentShutdownHook;
import ygo.traffichunter.agent.engine.context.AgentExecutableContext;
import ygo.traffichunter.agent.engine.context.configuration.ConfigurableContextInitializer;
import ygo.traffichunter.agent.engine.env.ConfigurableEnvironment;
import ygo.traffichunter.agent.event.listener.AgentStateEventListener;
import ygo.traffichunter.agent.event.object.AgentStateEvent;
import ygo.traffichunter.agent.event.store.AgentStateEventStore;

/**
 * The {@code TrafficHunterAgentExecutableContext} class represents the execution context
 * for the TrafficHunter Agent. It manages the agent's state, event listeners, environment
 * configuration, and shutdown operations.
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
public class TrafficHunterAgentExecutableContext extends AgentStateEventStore implements AgentExecutableContext {

    private final ConfigurableEnvironment environment;

    private final AtomicReference<AgentStatus> status = new AtomicReference<>(AgentStatus.INITIALIZED);

    private final TrafficHunterAgentShutdownHook shutdownHook;

    private final ReentrantLock shutdownLock = new ReentrantLock();

    private final AtomicBoolean isShutdown = new AtomicBoolean(false);

    public TrafficHunterAgentExecutableContext(final ConfigurableEnvironment environment,
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

        if(isStopped()) {
            return;
        }

        if(this.shutdownHook.isEnabledShutdownHook() && this.isShutdown.compareAndSet(false, true)) {
            Thread shutdownHookThread;
            shutdownLock.lock();
            try {
                shutdownHookThread = new Thread(this.shutdownHook, "TrafficHunterAgentShutdownHook");
                shutdownHookThread.start();
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                shutdownLock.unlock();
            }
        }
    }

    @Override
    public ConfigurableEnvironment getEnvironment() {
        return environment;
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
}
