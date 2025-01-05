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
package org.traffichunter.javaagent.jmx.collect.systeminfo.runtime;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.logging.Logger;
import javax.management.MBeanServerConnection;
import org.traffichunter.javaagent.jmx.collect.AbstractMBeanMetricCollector;
import org.traffichunter.javaagent.jmx.metric.systeminfo.runtime.RuntimeStatusInfo;

/**
 * @author yungwang-o
 * @version 1.0.0
 */
public class RuntimeMetricCollector extends AbstractMBeanMetricCollector<RuntimeStatusInfo> {

    private static final Logger log = Logger.getLogger(RuntimeStatusInfo.class.getName());

    @Override
    public RuntimeStatusInfo collect(final MBeanServerConnection mbsc) {
        try {
            final RuntimeMXBean runtimeMXBean = ManagementFactory.newPlatformMXBeanProxy(
                    mbsc,
                    ManagementFactory.RUNTIME_MXBEAN_NAME,
                    RuntimeMXBean.class
            );

            return new RuntimeStatusInfo(
                    runtimeMXBean.getStartTime(),
                    runtimeMXBean.getUptime(),
                    runtimeMXBean.getVmName(),
                    runtimeMXBean.getVmVersion()
            );
        } catch (IOException e) {
            log.warning("Failed to get runtime metric: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public RuntimeStatusInfo collect() {
        final RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();

        return new RuntimeStatusInfo(
                runtimeMXBean.getStartTime(),
                runtimeMXBean.getUptime(),
                runtimeMXBean.getVmName(),
                runtimeMXBean.getVmVersion()
        );
    }
}
