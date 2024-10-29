package ygo.traffichunter.agent.engine;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ygo.traffichunter.agent.engine.collect.MetricCollector;
import ygo.traffichunter.agent.engine.collect.cpu.CpuMetricCollector;
import ygo.traffichunter.agent.engine.collect.gc.GarbageCollectionMetricCollector;
import ygo.traffichunter.agent.engine.collect.memory.MemoryMetricCollector;
import ygo.traffichunter.agent.engine.collect.runtime.RuntimeMetricCollector;
import ygo.traffichunter.agent.engine.collect.thread.ThreadMetricCollector;
import ygo.traffichunter.agent.engine.instrument.collect.TransactionMetric;
import ygo.traffichunter.agent.engine.jvm.JVMSelector;
import ygo.traffichunter.agent.engine.sender.TrafficHunterAgentSender;
import ygo.traffichunter.agent.engine.systeminfo.SystemInfo;
import ygo.traffichunter.agent.engine.systeminfo.gc.GarbageCollectionStatusInfo;
import ygo.traffichunter.agent.engine.systeminfo.memory.MemoryStatusInfo;
import ygo.traffichunter.agent.engine.systeminfo.cpu.CpuStatusInfo;
import ygo.traffichunter.agent.engine.systeminfo.runtime.RuntimeStatusInfo;
import ygo.traffichunter.agent.engine.systeminfo.thread.ThreadStatusInfo;
import ygo.traffichunter.agent.event.consume.EventConsumer;
import ygo.traffichunter.agent.event.consume.TrafficHunterEventConsumer;
import ygo.traffichunter.agent.property.TrafficHunterAgentProperty;
import ygo.traffichunter.http.HttpBuilder;
import ygo.traffichunter.retry.RetryHelper;
import ygo.traffichunter.websocket.MetricWebSocketClient;

/**
 * After selecting a JVM, this agent execution engine collects the metrics of the JVM at regular intervals and transmits them to the server.
 * <br/>
 * <br/>
 * The transmission method is HTTP.
 * <br/>
 * <br/>
 * what metric is it?
 * <br/>
 * <br/>
 * garbage collection (GC), thread, tomcat, memory heap, cpu usage
 * <br/>
 */
public final class AgentExecutionEngine {

    private static final Logger log = LoggerFactory.getLogger(AgentExecutionEngine.class);

    private static final TrafficHunterAgentShutdownHook shutdownHook = new TrafficHunterAgentShutdownHook();

    private static final Scheduler scheduler = new Scheduler();

    public static void run(final TrafficHunterAgentProperty property) {

        scheduler.schedule(
                property.scheduleInterval(),
                property.timeUnit(),
                () -> new AgentSystemMetricSender(property));

        scheduler.exit();
    }

    private static class Scheduler {

        private final ScheduledExecutorService scheduledExecutor =
                Executors.newSingleThreadScheduledExecutor();

        private void schedule(final int interval, final TimeUnit timeUnit, final Runnable runnable) {
            scheduledExecutor.scheduleWithFixedDelay(runnable, 0, interval, timeUnit);
        }

        private void schedule(final Runnable runnable) {
            scheduledExecutor.scheduleWithFixedDelay(runnable, 0, 1, TimeUnit.SECONDS);
        }

        private void exit() {
            shutdownHook.addRuntimeShutdownHook(scheduledExecutor::shutdown);
        }
    }

    private record AgentSystemMetricSender(TrafficHunterAgentProperty property)
            implements TrafficHunterAgentSender<SystemInfo, Supplier<HttpResponse<String>>>, Runnable {

        @Override
        public void run() {
            final SystemInfo systemInfo = MetricCollectionSupport.collect(property.targetJVMPath());

            final HttpResponse<String> httpResponse = RetryHelper.start(property.backOffPolicy(), property.maxAttempt())
                    .failAfterMaxAttempts(true)
                    .retryName("httpResponse")
                    .throwable(throwable -> throwable instanceof RuntimeException)
                    .retrySupplier(this.toSend(systemInfo));

            log.info("httpResponse status = {}", httpResponse.statusCode());
        }

        @Override
        public Supplier<HttpResponse<String>> toSend(final SystemInfo systemInfo) {
            return () -> {
                try {
                    return HttpBuilder.newBuilder(property.uri())
                            .header("Content-Type", "application/json")
                            .timeOut(Duration.ofSeconds(3))
                            .request(systemInfo)
                            .build();
                } catch (Exception e) {
                    log.error("http request error = {}", e.getMessage());
                    throw new RuntimeException(e);
                }
            };
        }
    }

    /**
     * web socket
     * @param property
     * @param client
     */
    private record AgentTransactionMetricSender(TrafficHunterAgentProperty property, MetricWebSocketClient<TransactionMetric> client)
            implements TrafficHunterAgentSender<TransactionMetric, Void> {

        @Override
        public Void toSend(final TransactionMetric input) {
            RetryHelper.start(property.backOffPolicy(), property.maxAttempt())
                    .failAfterMaxAttempts(true)
                    .retryName("websocket")
                    .throwable(throwable -> throwable instanceof RuntimeException)
                    .retrySupplier(() -> {
                        try {
                            if(client.connect(3, TimeUnit.SECONDS)) {
                                client.toSend(input);
                            }
                        } catch (InterruptedException e) {
                            throw new IllegalStateException(e);
                        }

                        return null;
                    });

            return null;
        }
    }

    private static class MetricCollectionSupport {

        private static final MetricCollector<MemoryStatusInfo> collectMemory = new MemoryMetricCollector();

        private static final MetricCollector<CpuStatusInfo> collectCpu = new CpuMetricCollector();

        private static final MetricCollector<ThreadStatusInfo> collectThread = new ThreadMetricCollector();

        private static final MetricCollector<GarbageCollectionStatusInfo> collectorGC = new GarbageCollectionMetricCollector();

        private static final MetricCollector<RuntimeStatusInfo> collectorRuntime = new RuntimeMetricCollector();

        private MetricCollectionSupport() {
        }

        private static SystemInfo collect(final String targetJVMPath) {

            try (final JMXConnector jmxConnector = JMXConnectorFactory.connect(JVMSelector.getVMXServiceUrl(targetJVMPath))) {

                final MBeanServerConnection mbsc = jmxConnector.getMBeanServerConnection();

                return new SystemInfo(
                        Instant.now(),
                        targetJVMPath,
                        collectMemory.collect(mbsc),
                        collectThread.collect(mbsc),
                        collectCpu.collect(mbsc),
                        collectorGC.collect(mbsc),
                        collectorRuntime.collect(mbsc)
                );

            } catch (IOException e) {
                log.error("Failed to start local management agent = {}", e.getMessage());
                throw new RuntimeException(e);
            }
        }
    }
}
