package ygo.traffichunter.agent.engine.sender.manager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ygo.traffichunter.agent.AgentStatus;
import ygo.traffichunter.agent.engine.context.AgentExecutableContext;
import ygo.traffichunter.agent.engine.sender.http.AgentSystemMetricSender;
import ygo.traffichunter.agent.engine.sender.websocket.AgentTransactionMetricSender;
import ygo.traffichunter.agent.engine.systeminfo.metadata.AgentMetadata;
import ygo.traffichunter.agent.property.TrafficHunterAgentProperty;

public class MetricSendSessionManager {

    private static final Logger log = LoggerFactory.getLogger(MetricSendSessionManager.class);

    private final TrafficHunterAgentProperty property;

    private final AgentTransactionMetricSender transactionMetricSender;

    private final AgentSystemMetricSender systemMetricSender;

    private final ScheduledExecutorService schedule;

    private final ExecutorService executor;

    private final AgentExecutableContext context;

    private final AgentMetadata metadata;

    public MetricSendSessionManager(final TrafficHunterAgentProperty property,
                                    final AgentExecutableContext context,
                                    final AgentMetadata metadata) {

        this.metadata = metadata;
        this.context = context;
        this.property = property;
        this.transactionMetricSender = new AgentTransactionMetricSender(property);
        this.systemMetricSender = new AgentSystemMetricSender(property);
        this.schedule = Executors.newSingleThreadScheduledExecutor(getThreadFactory("TransactionSystemInfoMetricSender"));
        this.executor = Executors.newVirtualThreadPerTaskExecutor();
    }

    public void run() {

        if(context.isStopped()) {
            log.error("MetricSendSessionManager is stopped");
            return;
        }

        if(context.isRunning()) {
            log.error("MetricSendSessionManager is already running");
            return;
        }

        log.info("start Metric send...");

        context.setStatus(AgentStatus.RUNNING);

        executor.execute(() -> transactionMetricSender.toSend(metadata));

        schedule.scheduleWithFixedDelay(() -> systemMetricSender.toSend(metadata),
                0,
                property.scheduleInterval(),
                property.timeUnit()
        );
    }

    public void close() {
        log.info("closing MetricSendSessionManager...");
        context.setStatus(AgentStatus.EXIT);
        transactionMetricSender.close();
        executor.shutdown();
        schedule.shutdown();
    }

    private ThreadFactory getThreadFactory(final String threadName) {
        return r -> {
            Thread thread = new Thread(r);
            thread.setDaemon(true);
            thread.setName(threadName);

            return thread;
        };
    }
}
