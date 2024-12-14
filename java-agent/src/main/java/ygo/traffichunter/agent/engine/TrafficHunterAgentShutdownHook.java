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
