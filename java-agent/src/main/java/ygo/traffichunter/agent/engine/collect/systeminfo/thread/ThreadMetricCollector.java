package ygo.traffichunter.agent.engine.collect.systeminfo.thread;

import com.sun.management.ThreadMXBean;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.logging.Logger;
import javax.management.MBeanServerConnection;
import ygo.traffichunter.agent.engine.collect.MetricCollector;
import ygo.traffichunter.agent.engine.metric.systeminfo.thread.ThreadStatusInfo;

public class ThreadMetricCollector implements MetricCollector<ThreadStatusInfo> {

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
