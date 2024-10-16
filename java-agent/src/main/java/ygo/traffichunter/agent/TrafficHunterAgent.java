package ygo.traffichunter.agent;

import java.net.URI;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import ygo.traffichunter.agent.child.FaultTolerantTrafficHunterAgent;
import ygo.traffichunter.agent.property.TrafficHunterAgentProperty;

public class TrafficHunterAgent {

    private static final Logger log = Logger.getLogger(TrafficHunterAgent.class.getName());
    protected int scheduleInterval;
    protected String targetJVMPath;
    private final URI uri;
    protected TimeUnit timeUnit;

    protected TrafficHunterAgent(final URI uri) {
        this.uri = uri;
    }

    public static TrafficHunterAgent connect(final String serverUrl) {
        return new TrafficHunterAgent(URI.create(serverUrl));
    }

    @Deprecated(since = "1.0")
    public FaultTolerantTrafficHunterAgent faultTolerant() {
        return new FaultTolerantTrafficHunterAgent(uri);
    }

    public TrafficHunterAgent targetJVM(final String targetJVMPath) {
        this.targetJVMPath = targetJVMPath;
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
        return new TrafficHunterAgentProperty(targetJVMPath, scheduleInterval, uri, timeUnit);
    }
}
