package ygo.traffichunter.agent.engine.collect;

import java.io.IOException;
import java.time.Instant;
import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ygo.traffichunter.agent.engine.collect.dbcp.hikari.HikariCPMetricCollector;
import ygo.traffichunter.agent.engine.collect.systeminfo.cpu.CpuMetricCollector;
import ygo.traffichunter.agent.engine.collect.systeminfo.gc.GarbageCollectionMetricCollector;
import ygo.traffichunter.agent.engine.collect.systeminfo.memory.MemoryMetricCollector;
import ygo.traffichunter.agent.engine.collect.systeminfo.runtime.RuntimeMetricCollector;
import ygo.traffichunter.agent.engine.collect.systeminfo.thread.ThreadMetricCollector;
import ygo.traffichunter.agent.engine.collect.web.tomcat.TomcatMetricCollector;
import ygo.traffichunter.agent.engine.jvm.JVMSelector;
import ygo.traffichunter.agent.engine.metric.systeminfo.SystemInfo;
import ygo.traffichunter.agent.property.TrafficHunterAgentProperty;

/**
 * The {@code MetricCollectSupport} class provides functionality for collecting system metrics
 * from the local JVM or a target JVM via JMX.
 *
 * <p>Features:</p>
 * <ul>
 *     <li>Integrates multiple metric collectors for memory, CPU, threads, garbage collection,
 *         runtime, Tomcat, and HikariCP metrics.</li>
 *     <li>Supports collecting metrics from the local JVM or a remote JVM identified by a JMX path.</li>
 *     <li>Combines collected metrics into a unified {@link SystemInfo} object.</li>
 * </ul>
 *
 * @see SystemInfo
 * @see MemoryMetricCollector
 * @see CpuMetricCollector
 * @see ThreadMetricCollector
 * @see GarbageCollectionMetricCollector
 * @see RuntimeMetricCollector
 * @see TomcatMetricCollector
 * @see HikariCPMetricCollector
 *
 * @author yungwang-o
 * @version 1.0.0
 */

public class MetricCollectSupport {

    private static final Logger log = LoggerFactory.getLogger(MetricCollectSupport.class);

    private final MemoryMetricCollector collectorMemory;

    private final CpuMetricCollector collectorCpu;

    private final ThreadMetricCollector collectorThread;

    private final GarbageCollectionMetricCollector collectorGC;

    private final RuntimeMetricCollector collectorRuntime;

    private final TomcatMetricCollector collectorTomcat;

    private final HikariCPMetricCollector collectorHikari;

    public MetricCollectSupport(final TrafficHunterAgentProperty property) {
        this.collectorMemory = new MemoryMetricCollector();
        this.collectorCpu = new CpuMetricCollector();
        this.collectorThread = new ThreadMetricCollector();
        this.collectorGC = new GarbageCollectionMetricCollector();
        this.collectorRuntime = new RuntimeMetricCollector();
        this.collectorTomcat = new TomcatMetricCollector(property);
        this.collectorHikari = new HikariCPMetricCollector();
    }

    public SystemInfo collect(final String targetJVMPath) {

        try (final JMXConnector jmxConnector = JMXConnectorFactory.connect(JVMSelector.getVMXServiceUrl(targetJVMPath))) {

            final MBeanServerConnection mbsc = jmxConnector.getMBeanServerConnection();

            return new SystemInfo(
                    Instant.now(),
                    collectorMemory.collect(mbsc),
                    collectorThread.collect(mbsc),
                    collectorCpu.collect(mbsc),
                    collectorGC.collect(mbsc),
                    collectorRuntime.collect(mbsc),
                    collectorTomcat.collect(mbsc),
                    collectorHikari.collect(mbsc)
            );

        } catch (IOException e) {
            log.error("Failed to start local management agent = {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public SystemInfo collect() {
        return new SystemInfo(
                Instant.now(),
                collectorMemory.collect(),
                collectorThread.collect(),
                collectorCpu.collect(),
                collectorGC.collect(),
                collectorRuntime.collect(),
                collectorTomcat.collect(),
                collectorHikari.collect()
        );
    }
}
