package ygo.traffichunter.agent.engine;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.Logger;
import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import ygo.traffichunter.agent.engine.collect.MetricCollector;
import ygo.traffichunter.agent.engine.collect.cpu.CpuMetricCollector;
import ygo.traffichunter.agent.engine.collect.gc.GarbageCollectionMetricCollector;
import ygo.traffichunter.agent.engine.collect.memory.MemoryMetricCollector;
import ygo.traffichunter.agent.engine.collect.runtime.RuntimeMetricCollector;
import ygo.traffichunter.agent.engine.collect.thread.ThreadMetricCollector;
import ygo.traffichunter.agent.engine.jvm.JVMSelector;
import ygo.traffichunter.agent.engine.systeminfo.SystemInfo;
import ygo.traffichunter.agent.engine.systeminfo.gc.GarbageCollectionStatusInfo;
import ygo.traffichunter.agent.engine.systeminfo.memory.MemoryStatusInfo;
import ygo.traffichunter.agent.engine.systeminfo.cpu.CpuStatusInfo;
import ygo.traffichunter.agent.engine.systeminfo.runtime.RuntimeStatusInfo;
import ygo.traffichunter.agent.engine.systeminfo.thread.ThreadStatusInfo;
import ygo.traffichunter.agent.property.TrafficHunterAgentProperty;
import ygo.traffichunter.http.HttpBuilder;

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

    private static final Logger log = Logger.getLogger(AgentExecutionEngine.class.getName());

    public static void run(final TrafficHunterAgentProperty property) {

        final ScheduledExecutorService es = Executors.newSingleThreadScheduledExecutor();

        es.scheduleAtFixedRate(() -> {
            final SystemInfo systemInfo = execute(property.targetJVMPath());

            final HttpResponse<String> httpResponse = HttpBuilder.newBuilder(property.uri())
                    .header("Content-Type", "application/json")
                    .timeOut(Duration.ofSeconds(10))
                    .request(systemInfo)
                    .build();

            log.info("http status code : " + httpResponse.statusCode());

        }, 0, property.scheduleInterval(), property.timeUnit());
    }

    private static SystemInfo execute(final String targetJVMPath) {

        try (final JMXConnector jmxConnector = JMXConnectorFactory.connect(JVMSelector.getVM(targetJVMPath))) {

            final MBeanServerConnection mBeanServerConnection = jmxConnector.getMBeanServerConnection();

            return new MetricCollectionSupport(mBeanServerConnection).getSystemInfo();

        } catch (IOException e) {
            log.warning("Failed to start local management agent: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private static class MetricCollectionSupport {

        private final MBeanServerConnection mbsc;
        private final MetricCollector<MemoryStatusInfo> collectMemory = new MemoryMetricCollector();
        private final MetricCollector<CpuStatusInfo> collectCpu = new CpuMetricCollector();
        private final MetricCollector<ThreadStatusInfo> collectThread = new ThreadMetricCollector();
        private final MetricCollector<GarbageCollectionStatusInfo> collectorGC = new GarbageCollectionMetricCollector();
        private final MetricCollector<RuntimeStatusInfo> collectorRuntime = new RuntimeMetricCollector();

        private MetricCollectionSupport(final MBeanServerConnection mbsc) {
            this.mbsc = mbsc;
        }

        public SystemInfo getSystemInfo() {
            return new SystemInfo(
                    collectMemory.collect(mbsc),
                    collectThread.collect(mbsc),
                    collectCpu.collect(mbsc),
                    collectorGC.collect(mbsc),
                    collectorRuntime.collect(mbsc)
            );
        }
    }
}
