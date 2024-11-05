package ygo.traffichunter.agent.engine.context.execute;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;
import ygo.traffichunter.agent.AgentStatus;
import ygo.traffichunter.agent.engine.TrafficHunterAgentShutdownHook;
import ygo.traffichunter.agent.engine.context.AgentExecutableContext;
import ygo.traffichunter.agent.engine.context.configuration.ConfigurableContextInitializer;
import ygo.traffichunter.agent.engine.env.ConfigurableEnvironment;

public class TrafficHunterAgentExecutableContext implements AgentExecutableContext {

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
    public ConfigurableContextInitializer configureEnv() {
        return new ConfigurableContextInitializer(environment);
    }

    @Override
    public void close() {

        if(isStopped()) {
            return;
        }

        if(this.shutdownHook.isEnabledShutdownHook() && this.isShutdown.compareAndSet(false, true)) {
            setStatus(AgentStatus.EXIT);
            Thread shutdownHookThread;
            shutdownLock.lock();
            try {
                shutdownHookThread = new Thread(this.shutdownHook);
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
    public boolean setStatus(final AgentStatus newStatus) {
        AgentStatus agentStatus = status.get();
        do {

            if(agentStatus.equals(AgentStatus.EXIT)) {
                return false;
            }

        } while (!status.compareAndSet(agentStatus, newStatus));

        return true;
    }
}
