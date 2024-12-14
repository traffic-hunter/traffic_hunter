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
package ygo.traffichunter.agent.engine.context;

import ygo.traffichunter.agent.AgentStatus;
import ygo.traffichunter.agent.engine.context.configuration.ConfigurableContextInitializer;
import ygo.traffichunter.agent.engine.env.ConfigurableEnvironment;
import ygo.traffichunter.agent.event.context.AgentContextStateEventHandler;

/**
 * The {@code AgentExecutableContext} interface defines the contract for managing
 * the lifecycle of an agent's execution context. It includes methods for initializing
 * the context, managing its state, and handling lifecycle events.
 *
 * <p>Features:</p>
 * <ul>
 *     <li>Extends {@link AgentContextStateEventHandler} for state event listener management.</li>
 *     <li>Provides methods to initialize, close, and query the context's status.</li>
 *     <li>Supports dynamic updates to the agent's execution status.</li>
 * </ul>
 *
 * @see AgentContextStateEventHandler
 * @see ConfigurableContextInitializer
 * @see ConfigurableEnvironment
 * @see AgentStatus
 *
 * @author yungwang-o
 * @version 1.0.0
 */
public interface AgentExecutableContext extends AgentContextStateEventHandler {

    ConfigurableContextInitializer init();

    void close();

    ConfigurableEnvironment getEnvironment();

    AgentStatus getStatus();

    boolean isInit();

    boolean isRunning();

    boolean isStopped();

    void setStatus(AgentStatus status);
}
