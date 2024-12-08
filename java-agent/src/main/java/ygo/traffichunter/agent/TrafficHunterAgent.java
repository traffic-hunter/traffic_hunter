package ygo.traffichunter.agent;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import ygo.traffichunter.agent.child.FaultTolerantTrafficHunterAgent;
import ygo.traffichunter.agent.property.TrafficHunterAgentProperty;

/**
 * The {@code TrafficHunterAgent} class is responsible for holding and managing configuration
 * details required to set up and execute traffic monitoring or management tasks.
 * This class provides a fluent API for constructing and customizing the configuration
 * properties, such as scheduling intervals, target URIs, and resource locations.
 *
 * <p>Purpose:</p>
 * <ul>
 *     <li>Encapsulates all necessary configuration details for an agent instance.</li>
 *     <li>Provides a builder-like pattern for step-by-step configuration using fluent methods.</li>
 *     <li>Finalizes the configuration into an immutable {@code TrafficHunterAgentProperty} object
 *         to ensure stability during execution.</li>
 * </ul>
 *
 * <p>Key Configuration Parameters:</p>
 * <ul>
 *     <li>{@code name} - The unique name identifying this agent instance.</li>
 *     <li>{@code targetUri} - The target URI for monitoring or traffic management.</li>
 *     <li>{@code jar} - The path to the agent's executable JAR file.</li>
 *     <li>{@code scheduleInterval} - The interval between scheduled executions.</li>
 *     <li>{@code timeUnit} - The time unit for the scheduling interval.</li>
 *     <li>{@code uri} - The base server URI to connect.</li>
 * </ul>
 *
 * <p>Example Usage:</p>
 * <pre>{@code
 * TrafficHunterAgent agent = TrafficHunterAgent.connect("localhost:8080")
 *     .name("MyAgent")
 *     .targetUri("localhost:8888")
 *     .locationJar("/path/to/agent.jar")
 *     .scheduleInterval(10)
 *     .scheduleTimeUnit(TimeUnit.MINUTES)
 *     .complete();
 *
 * TrafficHunterAgentProperty properties = agent.complete();
 * System.out.println("Agent name: " + properties.getName());
 * }</pre>
 *
 * <p>Thread Safety:</p>
 * <ul>
 *     <li>This class is not thread-safe. Avoid sharing a single instance across multiple threads.</li>
 * </ul>
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

    private static final Logger log = Logger.getLogger(TrafficHunterAgent.class.getName());

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
