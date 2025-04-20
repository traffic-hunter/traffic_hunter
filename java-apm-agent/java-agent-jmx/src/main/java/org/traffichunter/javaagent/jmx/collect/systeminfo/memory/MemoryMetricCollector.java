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
package org.traffichunter.javaagent.jmx.collect.systeminfo.memory;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.util.logging.Logger;
import org.traffichunter.javaagent.jmx.collect.AbstractMBeanMetricCollector;
import org.traffichunter.javaagent.jmx.metric.systeminfo.memory.MemoryStatusInfo;
import org.traffichunter.javaagent.jmx.metric.systeminfo.memory.MemoryStatusInfo.MemoryUsage;

/**
 * @author yungwang-o
 * @version 1.0.0
 */
public class MemoryMetricCollector extends AbstractMBeanMetricCollector<MemoryStatusInfo> {

    private static final Logger log = Logger.getLogger(MemoryMetricCollector.class.getName());

    @Override
    public MemoryStatusInfo collect() {
        final MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();

        final java.lang.management.MemoryUsage heapMemoryUsage = memoryMXBean.getHeapMemoryUsage();
        final java.lang.management.MemoryUsage nonHeapMemoryUsage = memoryMXBean.getNonHeapMemoryUsage();

        return new MemoryStatusInfo(
                new MemoryUsage(
                        heapMemoryUsage.getInit(),
                        heapMemoryUsage.getUsed(),
                        heapMemoryUsage.getCommitted(),
                        heapMemoryUsage.getMax()
                ),
                new MemoryUsage(
                        nonHeapMemoryUsage.getInit(),
                        nonHeapMemoryUsage.getUsed(),
                        nonHeapMemoryUsage.getCommitted(),
                        nonHeapMemoryUsage.getMax()
                )
        );
    }
}
