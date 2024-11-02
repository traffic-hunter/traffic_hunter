package ygo.traffichunter.agent.engine.collect;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ygo.traffichunter.agent.engine.collect.cpu.CpuMetricCollector;
import ygo.traffichunter.agent.engine.collect.gc.GarbageCollectionMetricCollector;
import ygo.traffichunter.agent.engine.collect.memory.MemoryMetricCollector;
import ygo.traffichunter.agent.engine.collect.runtime.RuntimeMetricCollector;
import ygo.traffichunter.agent.engine.collect.thread.ThreadMetricCollector;
import ygo.traffichunter.agent.engine.collect.transaction.TransactionMetricCollector;
import ygo.traffichunter.agent.engine.jvm.JVMSelector;
import ygo.traffichunter.agent.engine.systeminfo.SystemInfo;
import ygo.traffichunter.agent.engine.systeminfo.TransactionInfo;

public class MetricCollectSupport {

    private static final Logger log = LoggerFactory.getLogger(MetricCollectSupport.class);

    private static final MemoryMetricCollector collectMemory = new MemoryMetricCollector();

    private static final CpuMetricCollector collectCpu = new CpuMetricCollector();

    private static final ThreadMetricCollector collectThread = new ThreadMetricCollector();

    private static final GarbageCollectionMetricCollector collectorGC = new GarbageCollectionMetricCollector();

    private static final RuntimeMetricCollector collectorRuntime = new RuntimeMetricCollector();

    private static final TransactionMetricCollector collectorTransaction = new TransactionMetricCollector();

    private MetricCollectSupport() {
    }

    public static List<TransactionInfo> collect() {
        return collectorTransaction.collect();
    }

    public static SystemInfo collect(final String targetJVMPath) {

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
