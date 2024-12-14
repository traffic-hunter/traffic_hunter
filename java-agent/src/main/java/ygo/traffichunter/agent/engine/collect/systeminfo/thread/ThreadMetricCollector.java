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
package ygo.traffichunter.agent.engine.collect.systeminfo.thread;

import com.sun.management.ThreadMXBean;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.logging.Logger;
import javax.management.MBeanServerConnection;
import ygo.traffichunter.agent.engine.collect.AbstractMBeanMetricCollector;
import ygo.traffichunter.agent.engine.metric.systeminfo.thread.ThreadStatusInfo;

/**
 * @author yungwang-o
 * @version 1.0.0
 */
public class ThreadMetricCollector extends AbstractMBeanMetricCollector<ThreadStatusInfo> {

    private static final Logger log = Logger.getLogger(ThreadMetricCollector.class.getName());

    @Override
    public ThreadStatusInfo collect(final MBeanServerConnection mbsc) {
        try {
            final ThreadMXBean threadMXBean = ManagementFactory.newPlatformMXBeanProxy(
                    mbsc,
                    ManagementFactory.THREAD_MXBEAN_NAME,
                    ThreadMXBean.class
            );

            return new ThreadStatusInfo(
                    threadMXBean.getThreadCount(),
                    threadMXBean.getPeakThreadCount(),
                    threadMXBean.getTotalStartedThreadCount()
            );
        } catch (IOException e) {
            log.warning("Failed to get thread metric: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public ThreadStatusInfo collect() {
        final ThreadMXBean threadMXBean = (ThreadMXBean) ManagementFactory.getThreadMXBean();

        return new ThreadStatusInfo(
                threadMXBean.getThreadCount(),
                threadMXBean.getPeakThreadCount(),
                threadMXBean.getTotalStartedThreadCount()
        );
    }
}
