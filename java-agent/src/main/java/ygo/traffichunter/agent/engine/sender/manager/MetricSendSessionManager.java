package ygo.traffichunter.agent.engine.sender.manager;

import java.io.Closeable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ygo.traffichunter.agent.AgentStatus;
import ygo.traffichunter.agent.engine.context.AgentExecutableContext;
import ygo.traffichunter.agent.engine.sender.http.AgentSystemMetricSender;
import ygo.traffichunter.agent.engine.sender.websocket.AgentTransactionMetricSender;
import ygo.traffichunter.agent.property.TrafficHunterAgentProperty;

public class MetricSendSessionManager implements Closeable {

    private static final Logger log = LoggerFactory.getLogger(MetricSendSessionManager.class);

    private final TrafficHunterAgentProperty property;

    private final AgentTransactionMetricSender transactionMetricSender;

    private final AgentSystemMetricSender systemMetricSender;

    private final ScheduledExecutorService schedule;

    private final ExecutorService executor;

    private final AgentExecutableContext context;

    public MetricSendSessionManager(final TrafficHunterAgentProperty property,
                                    final AgentExecutableContext context) {

        this.context = context;
        this.property = property;
        this.transactionMetricSender = new AgentTransactionMetricSender(property, context);
        this.systemMetricSender = new AgentSystemMetricSender(property, context);
        this.schedule = Executors.newSingleThreadScheduledExecutor();
        this.executor = Executors.newFixedThreadPool(4);
    }

    public void start() {
        if(!context.setStatus(AgentStatus.RUNNING)) {
            log.warn("MetricSendSessionManager not started");
            return;
        }

        log.info("starting MetricSendSessionManager...");

        executor.submit(() -> {
            try {
                transactionMetricSender.run();
            } catch (RuntimeException e) {
                context.setStatus(AgentStatus.ERROR);
                log.error("MetricSendSessionManager failed", e);
            }
        });

        schedule.scheduleWithFixedDelay(
                systemMetricSender,
                0,
                property.scheduleInterval(),
                property.timeUnit()
        );
    }

    @Override
    public void close() {
        log.info("closing MetricSendSessionManager...");

        schedule.shutdown();
        executor.shutdown();
    }

    public AgentStatus getStatus() {
        return context.getStatus();
    }
}
