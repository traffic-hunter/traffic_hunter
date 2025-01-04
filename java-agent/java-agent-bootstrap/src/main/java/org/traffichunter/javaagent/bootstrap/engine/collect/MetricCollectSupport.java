/**
 * The MIT License
 *
 * Copyright (c) 2024 yungwang-o
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.traffichunter.javaagent.bootstrap.engine.collect;

import java.io.IOException;
import java.time.Instant;
import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.traffichunter.javaagent.bootstrap.engine.collect.dbcp.hikari.HikariCPMetricCollector;
import org.traffichunter.javaagent.bootstrap.engine.collect.systeminfo.cpu.CpuMetricCollector;
import org.traffichunter.javaagent.bootstrap.engine.collect.systeminfo.gc.GarbageCollectionMetricCollector;
import org.traffichunter.javaagent.bootstrap.engine.collect.systeminfo.memory.MemoryMetricCollector;
import org.traffichunter.javaagent.bootstrap.engine.collect.systeminfo.runtime.RuntimeMetricCollector;
import org.traffichunter.javaagent.bootstrap.engine.collect.systeminfo.thread.ThreadMetricCollector;
import org.traffichunter.javaagent.bootstrap.engine.collect.web.tomcat.TomcatMetricCollector;
import org.traffichunter.javaagent.bootstrap.engine.jvm.JVMSelector;
import org.traffichunter.javaagent.bootstrap.engine.property.TrafficHunterAgentProperty;
import org.traffichunter.javaagent.commons.dto.systeminfo.SystemInfo;

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
