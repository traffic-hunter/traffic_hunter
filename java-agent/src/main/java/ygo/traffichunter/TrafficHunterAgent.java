package ygo.traffichunter;

import java.net.URI;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import ygo.traffichunter.engine.AgentEngine;
import ygo.traffichunter.engine.systeminfo.SystemInfo;
import ygo.traffichunter.websocket.MetricWebSocketClient;

public class TrafficHunterAgent {

    private static final Logger log = Logger.getLogger(TrafficHunterAgent.class.getName());
    private final MetricWebSocketClient webSocketClient;
    private final ScheduledExecutorService scheduledExecutorService;
    private int scheduleInterval;
    private int pid;
    private TimeUnit timeUnit;

    private TrafficHunterAgent(final MetricWebSocketClient webSocketClient,
                               final ScheduledExecutorService scheduledExecutorService) {

        log.info("traffic hunter agent started!!");
        this.webSocketClient = webSocketClient;
        this.scheduledExecutorService = scheduledExecutorService;
    }

    public static TrafficHunterAgent connect(final String serverUrl) {
        return new TrafficHunterAgent(
                new MetricWebSocketClient(URI.create(serverUrl)),
                Executors.newSingleThreadScheduledExecutor()
        );
    }

    public TrafficHunterAgent vmPID(final int pid) {
        this.pid = pid;

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

    public void execute() {
        webSocketClient.connect();

        final SystemInfo systemInfo = AgentEngine.connect()
                .pid(pid)
                .execute();

        webSocketClient.toSend(systemInfo);
    }
}
