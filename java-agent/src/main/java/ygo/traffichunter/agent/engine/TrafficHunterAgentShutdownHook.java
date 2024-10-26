package ygo.traffichunter.agent.engine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class TrafficHunterAgentShutdownHook {

    private static final Logger log = LoggerFactory.getLogger(TrafficHunterAgentShutdownHook.class);

    private volatile boolean enabledShutdownHook = false;

    public synchronized void addRuntimeShutdownHook(final Runnable runnable) {

        if(enabledShutdownHook) {
            return;
        }

        enabledShutdownHook = true;

        Runtime.getRuntime().addShutdownHook(new Thread(runnable, "TrafficHunterShutdownHook"));
    }
}
