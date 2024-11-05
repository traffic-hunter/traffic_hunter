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

public class MetricSendSessionManager {

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
        this.executor = Executors.newFixedThreadPool(3);
    }

    public void run() {
        if(context.isRunning()) {
            log.error("MetricSendSessionManager already started!!");
            return;
        }

        log.info("start Metric send...");

        executor.submit(() -> {
            Thread.currentThread().setName("transaction-send-thread");
            try {
                if(!context.setStatus(AgentStatus.RUNNING)) {
                    log.warn("Failed to set agent transaction sender status to RUNNING");
                    return;
                }

                log.info("sending transaction metric");
                transactionMetricSender.run();
            } catch (Exception e) {
                context.setStatus(AgentStatus.ERROR);
                log.error("MetricSendSessionManager transaction tracker failed = {}", e.getMessage());
            }
        });

        schedule.scheduleWithFixedDelay(
                () -> {
                    Thread.currentThread().setName("system-metric-send-thread");
                    try {
                        if(!context.setStatus(AgentStatus.RUNNING)) {
                            log.warn("Failed to set agent system metric sender status to RUNNING");
                            return;
                        }

                        log.info("metric send");
                        while (!context.isStopped()) {
                            systemMetricSender.run();
                        }
                    } catch (Exception e) {
                        context.setStatus(AgentStatus.ERROR);
                        log.error("MetricSendSessionManager system metric failed = {}", e.getMessage());
                    }
                },
                0,
                property.scheduleInterval(),
                property.timeUnit()
        );
    }

    public void close() {
        log.info("closing MetricSendSessionManager...");
        transactionMetricSender.close();
        schedule.shutdown();
        executor.shutdown();
    }

    public AgentStatus getStatus() {
        return context.getStatus();
    }
}
