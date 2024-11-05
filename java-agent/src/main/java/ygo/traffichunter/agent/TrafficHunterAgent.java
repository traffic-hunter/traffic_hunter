package ygo.traffichunter.agent;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import ygo.traffichunter.agent.child.FaultTolerantTrafficHunterAgent;
import ygo.traffichunter.agent.property.TrafficHunterAgentProperty;

public class TrafficHunterAgent {

    private static final Logger log = Logger.getLogger(TrafficHunterAgent.class.getName());

    protected int scheduleInterval;

    protected String targetJVMPath;

    protected String jar;

    protected final String uri;

    protected TimeUnit timeUnit;

    protected TrafficHunterAgent(final TrafficHunterAgent trafficHunterAgent) {
        this.scheduleInterval = trafficHunterAgent.scheduleInterval;
        this.targetJVMPath = trafficHunterAgent.targetJVMPath;
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

    public TrafficHunterAgent targetJVM(final String targetJVMPath) {
        this.targetJVMPath = targetJVMPath;
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
        return new TrafficHunterAgentProperty(targetJVMPath, jar, scheduleInterval, uri, timeUnit);
    }
}
