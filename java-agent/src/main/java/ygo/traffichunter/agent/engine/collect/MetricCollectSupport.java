package ygo.traffichunter.agent.engine.collect;

import java.io.IOException;
import java.time.Instant;
import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ygo.traffichunter.agent.engine.collect.systeminfo.cpu.CpuMetricCollector;
import ygo.traffichunter.agent.engine.collect.systeminfo.gc.GarbageCollectionMetricCollector;
import ygo.traffichunter.agent.engine.collect.systeminfo.memory.MemoryMetricCollector;
import ygo.traffichunter.agent.engine.collect.systeminfo.runtime.RuntimeMetricCollector;
import ygo.traffichunter.agent.engine.collect.systeminfo.thread.ThreadMetricCollector;
import ygo.traffichunter.agent.engine.jvm.JVMSelector;
import ygo.traffichunter.agent.engine.metric.systeminfo.SystemInfo;

public class MetricCollectSupport {

    private static final Logger log = LoggerFactory.getLogger(MetricCollectSupport.class);

    private static final MemoryMetricCollector collectMemory = new MemoryMetricCollector();

    private static final CpuMetricCollector collectCpu = new CpuMetricCollector();

    private static final ThreadMetricCollector collectThread = new ThreadMetricCollector();

    private static final GarbageCollectionMetricCollector collectorGC = new GarbageCollectionMetricCollector();

    private static final RuntimeMetricCollector collectorRuntime = new RuntimeMetricCollector();

    private MetricCollectSupport() {
    }

    public static SystemInfo collect(final String targetJVMPath) {

        try (final JMXConnector jmxConnector = JMXConnectorFactory.connect(JVMSelector.getVMXServiceUrl(targetJVMPath))) {

            final MBeanServerConnection mbsc = jmxConnector.getMBeanServerConnection();

            return new SystemInfo(
                    Instant.now(),
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

    public static SystemInfo collect() {
        return new SystemInfo(
                Instant.now(),
                collectMemory.collect(),
                collectThread.collect(),
                collectCpu.collect(),
                collectorGC.collect(),
                collectorRuntime.collect()
        );
    }
}
