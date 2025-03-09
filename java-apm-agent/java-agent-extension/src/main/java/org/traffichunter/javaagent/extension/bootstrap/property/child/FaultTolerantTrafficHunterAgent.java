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
package org.traffichunter.javaagent.extension.bootstrap.property.child;

import java.util.logging.Logger;
import org.traffichunter.javaagent.extension.bootstrap.property.TrafficHunterAgent;
import org.traffichunter.javaagent.extension.bootstrap.property.TrafficHunterAgentProperty;
import org.traffichunter.javaagent.retry.backoff.BackOffPolicy;

/**
 * The {@code FaultTolerantTrafficHunterAgent} class extends the functionality of
 * {@link TrafficHunterAgent} by adding fault-tolerant capabilities, such as retry logic
 * and back-off policies. This class is designed to handle scenarios where failures
 * may occur and ensures that the agent can recover gracefully.
 *
 * <p>Example Usage:</p>
 * <pre>{@code
 * TrafficHunterProperty property = TrafficHunterAgent.connect("localhost:8080")
 *     .name("ResilientAgent")
 *     .targetUri("localhost:8888")
 *     .locationJar("/path/to/agent.jar")
 *     .scheduleInterval(10)
 *     .scheduleTimeUnit(TimeUnit.MINUTES)
 *     .faultTolerant()
 *     .retry(5) // Set maximum retry attempts to 5
 *     .backOffPolicy(new ExponentialBackOffPolicy()) // Use exponential back-off policy
 *     .complete();
 * }</pre>
 *
 * @see TrafficHunterAgent
 * @see BackOffPolicy
 * @author yungwang-o
 * @version 1.0.0
 */
public class FaultTolerantTrafficHunterAgent extends TrafficHunterAgent {

    private static final Logger log = Logger.getLogger(FaultTolerantTrafficHunterAgent.class.getName());

    private BackOffPolicy backOffPolicy;

    private int maxAttempt;

    public FaultTolerantTrafficHunterAgent(final TrafficHunterAgent trafficHunterAgent) {
        super(trafficHunterAgent);
    }

    public FaultTolerantTrafficHunterAgent retry(final int maxAttempt) {
        this.maxAttempt = maxAttempt;
        return this;
    }

    public FaultTolerantTrafficHunterAgent backOffPolicy(final BackOffPolicy backOffPolicy) {
        this.backOffPolicy = backOffPolicy;
        return this;
    }

    @Override
    public TrafficHunterAgentProperty complete() {
        return new TrafficHunterAgentProperty(
                this.name,
                this.targetUri,
                this.jar,
                this.scheduleInterval,
                this.uri,
                this.timeUnit,
                maxAttempt,
                backOffPolicy
        );
    }
}
