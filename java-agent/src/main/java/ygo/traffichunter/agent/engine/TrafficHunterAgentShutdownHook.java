package ygo.traffichunter.agent.engine;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@code TrafficHunterAgentShutdownHook} class is responsible for managing and
 * registering shutdown hooks to execute specific actions when the Java application
 * terminates. This class allows users to add custom {@link Runnable} actions that
 * will be executed gracefully during the shutdown process.
 *
 * @see Runtime#addShutdownHook(Thread)
 * @see Runnable
 * @author yungwang-o
 * @version 1.0.0
 */
public class TrafficHunterAgentShutdownHook implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(TrafficHunterAgentShutdownHook.class);

    private volatile boolean enabledShutdownHook = false;

    private final Set<Runnable> actions = ConcurrentHashMap.newKeySet();

    public void enableShutdownHook() {
        enabledShutdownHook = true;
    }

    public void addRuntimeShutdownHook(final Runnable action) {
        actions.add(action);
    }

    @Override
    public void run() {
        log.info("register shutdown hook");

        if(!enabledShutdownHook) {
            log.info("shutdown hook not enabled");
            return;
        }

        for(Runnable action : actions) {
            Runtime.getRuntime().addShutdownHook(new Thread(action, nameShutdownThread(action.getClass().getName())));
        }
    }

    public boolean isEnabledShutdownHook() {
        return enabledShutdownHook;
    }

    private static String nameShutdownThread(final String threadName) {
        return threadName + "-ShutdownHook";
    }
}
