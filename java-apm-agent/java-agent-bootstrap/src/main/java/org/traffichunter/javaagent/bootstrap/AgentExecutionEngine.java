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
package org.traffichunter.javaagent.bootstrap;

import java.io.File;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Constructor;
import java.time.Duration;
import java.time.Instant;

/**
 * @author yungwang-o
 * @version 1.0.0
 */
public final class AgentExecutionEngine {

    private static final BootstrapLogger log = BootstrapLogger.getLogger(AgentExecutionEngine.class);

    private static final String CALL_AGENT_STARTER =
            "org.traffichunter.javaagent.extension.bootstrap.TrafficHunterAgentStartAction";

    private final TrafficHunterAgentShutdownHook shutdownHook = new TrafficHunterAgentShutdownHook();

    private final String agentArgs;

    private final Instrumentation inst;

    private final File agentBootstrapJar;

    private AgentExecutionEngine(final File agentBootstrapJar, final String args, final Instrumentation inst) {
        this.inst = inst;
        this.agentArgs = args;
        this.agentBootstrapJar = agentBootstrapJar;
    }

    private void run() {
        StartUp startUp = new StartUp();
        Instant startTime = startUp.getStartTime();

        if(AgentExecutionEngine.class.getClassLoader() != null) {
            throw new IllegalStateException("AgentExecutionEngine is not loaded in bootstrap class loader");
        }

        if(!shutdownHook.isEnabledShutdownHook()) {
            shutdownHook.enableShutdownHook();
        }

        try {

            InstrumentationHolder.setInstrumentation(inst);

            ClassLoader agentClassLoader = getAgentClassLoader(agentBootstrapJar);

            TrafficHunterAgentStarter trafficHunterAgentStarter = startAgent(agentClassLoader);

            trafficHunterAgentStarter.start(inst, agentArgs, startTime);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to start agent", e);
        }

        log.info("Started TrafficHunter Agent in {} second", startUp.getUpTime());
    }

    public static void run(final File agentBootstrapJar, final String args, final Instrumentation inst) {
        new AgentExecutionEngine(agentBootstrapJar, System.getProperty(args), inst).run();
    }

    /**
     * The {@code StartUp} class extends {@link LifeCycle} to measure the agent's
     * startup durations.
     *
     * <p>Features:</p>
     * <ul>
     *     <li>Tracks the agent's start time and end time.</li>
     *     <li>Calculates the total uptime.</li>
     * </ul>
     */
    private static class StartUp extends LifeCycle {

        public StartUp() {
            super();
        }

        @Override
        public Instant getStartTime() {
            return this.startTime;
        }

        @Override
        public Instant getEndTime() {
            if(endTime == null) {
                this.endTime = Instant.now();
            }
            return endTime;
        }

        @Override
        public Double getUpTime() {
            if(getStartTime() == null && getEndTime() == null) {
                throw new IllegalStateException("No start time or end time specified");
            }

            return Duration.between(getStartTime(), getEndTime()).toMillis() / 1_000.0;
        }
    }

    private TrafficHunterAgentStarter startAgent(final ClassLoader agentClassLoader) throws Exception {

        Class<?> agentStartAction = agentClassLoader.loadClass(CALL_AGENT_STARTER);

        Constructor<?> agentStartActionConstructor = agentStartAction.getDeclaredConstructor(TrafficHunterAgentShutdownHook.class);

        return (TrafficHunterAgentStarter) agentStartActionConstructor.newInstance(shutdownHook);
    }

    private static ClassLoader getAgentClassLoader(final File agentFile) {

        return new TrafficHunterAgentClassLoader(agentFile);
    }
}
