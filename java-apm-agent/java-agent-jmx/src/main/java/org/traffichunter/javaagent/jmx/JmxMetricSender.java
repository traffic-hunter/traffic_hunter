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
package org.traffichunter.javaagent.jmx;

import java.time.Instant;
import java.util.logging.Logger;
import org.traffichunter.javaagent.jmx.collect.dbcp.hikari.HikariCPMetricCollector;
import org.traffichunter.javaagent.jmx.collect.systeminfo.cpu.CpuMetricCollector;
import org.traffichunter.javaagent.jmx.collect.systeminfo.gc.GarbageCollectionMetricCollector;
import org.traffichunter.javaagent.jmx.collect.systeminfo.memory.MemoryMetricCollector;
import org.traffichunter.javaagent.jmx.collect.systeminfo.runtime.RuntimeMetricCollector;
import org.traffichunter.javaagent.jmx.collect.systeminfo.thread.ThreadMetricCollector;
import org.traffichunter.javaagent.jmx.collect.web.tomcat.TomcatMetricCollector;
import org.traffichunter.javaagent.jmx.metric.systeminfo.SystemInfo;

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

public class JmxMetricSender {

    private static final Logger log = Logger.getLogger(JmxMetricSender.class.getName());

    private final MemoryMetricCollector collectorMemory;

    private final CpuMetricCollector collectorCpu;

    private final ThreadMetricCollector collectorThread;

    private final GarbageCollectionMetricCollector collectorGC;

    private final RuntimeMetricCollector collectorRuntime;

    private final TomcatMetricCollector collectorTomcat;

    private final HikariCPMetricCollector collectorHikari;

    public JmxMetricSender(final String targetUri) {
        this.collectorMemory = new MemoryMetricCollector();
        this.collectorCpu = new CpuMetricCollector();
        this.collectorThread = new ThreadMetricCollector();
        this.collectorGC = new GarbageCollectionMetricCollector();
        this.collectorRuntime = new RuntimeMetricCollector();
        this.collectorTomcat = new TomcatMetricCollector(targetUri);
        this.collectorHikari = new HikariCPMetricCollector();
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
