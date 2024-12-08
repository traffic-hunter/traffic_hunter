package ygo.traffichunter.agent.engine.instrument.bootstrap;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author yungwang-o
 * @version 1.0.0
 */
public class BootState {

    private static final Boolean STATE_NONE = false;
    private static final Boolean STATE_STARTED = true;

    private final AtomicBoolean state = new AtomicBoolean(STATE_NONE);

    boolean getState() {
        return state.get();
    }

    public boolean start() {
        return state.compareAndSet(STATE_NONE, STATE_STARTED);
    }
}
