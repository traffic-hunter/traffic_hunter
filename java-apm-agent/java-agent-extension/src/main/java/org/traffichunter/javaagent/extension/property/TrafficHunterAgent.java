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
package org.traffichunter.javaagent.extension.property;

import java.util.concurrent.TimeUnit;
import org.traffichunter.javaagent.extension.property.child.FaultTolerantTrafficHunterAgent;

/**
 * <p>
 * The {@code TrafficHunterAgent} class is responsible for holding and managing configuration
 * details required to set up and execute traffic monitoring or management tasks.
 * This class provides a fluent API for constructing and customizing the configuration
 * properties, such as scheduling intervals, target URIs, and resource locations.
 * </p>
 *
 * <p>Example Usage:</p>
 * <pre>{@code
 * TrafficHunterProperty property = TrafficHunterAgent.connect("localhost:8080")
 *     .name("MyAgent")
 *     .targetUri("localhost:8888")
 *     .locationJar("/path/to/agent.jar")
 *     .scheduleInterval(10)
 *     .scheduleTimeUnit(TimeUnit.MINUTES)
 *     .complete();
 * }</pre>
 *
 * <p>Extensibility:</p>
 * <ul>
 *   <li>The class can be extended with custom behavior, as seen in {@link FaultTolerantTrafficHunterAgent}.</li>
 * </ul>
 *
 * @author yungwang-o
 * @version 1.0.0
 */
public class TrafficHunterAgent {

    protected int scheduleInterval;

    protected String targetUri;

    protected String jar;

    protected final String uri;

    protected TimeUnit timeUnit;

    protected String name;

    protected TrafficHunterAgent(final TrafficHunterAgent trafficHunterAgent) {
        this.name = trafficHunterAgent.name;
        this.scheduleInterval = trafficHunterAgent.scheduleInterval;
        this.targetUri = trafficHunterAgent.targetUri;
        this.uri = trafficHunterAgent.uri;
        this.timeUnit = trafficHunterAgent.timeUnit;
        this.jar = trafficHunterAgent.jar;
    }

    protected TrafficHunterAgent(final String uri) {
        this.uri = uri;
    }

    public static TrafficHunterAgent connect(final String serverUrl) {
        return new TrafficHunterAgent(serverUrl);
    }

    public FaultTolerantTrafficHunterAgent faultTolerant() {
        return new FaultTolerantTrafficHunterAgent(this);
    }

    public TrafficHunterAgent name(final String name) {
        this.name = name;
        return this;
    }

    public TrafficHunterAgent targetUri(final String targetUri) {
        this.targetUri = targetUri;
        return this;
    }

    public TrafficHunterAgent locationJar(final String jar) {
        this.jar = jar;
        return this;
    }

    public TrafficHunterAgent scheduleInterval(final int scheduleInterval) {
        this.scheduleInterval = scheduleInterval;
        return this;
    }

    public TrafficHunterAgent scheduleTimeUnit(final TimeUnit timeUnit) {
        this.timeUnit = timeUnit;
        return this;
    }

    public TrafficHunterAgentProperty complete() {
        return new TrafficHunterAgentProperty(name, targetUri, jar, scheduleInterval, uri, timeUnit);
    }
}
