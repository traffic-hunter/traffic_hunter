package ygo.traffichunter.agent.engine.collect.systeminfo.memory;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.util.logging.Logger;
import javax.management.MBeanServerConnection;
import ygo.traffichunter.agent.engine.collect.AbstractMBeanMetricCollector;
import ygo.traffichunter.agent.engine.collect.MetricCollector;
import ygo.traffichunter.agent.engine.metric.systeminfo.memory.MemoryStatusInfo;
import ygo.traffichunter.agent.engine.metric.systeminfo.memory.MemoryStatusInfo.MemoryUsage;

public class MemoryMetricCollector extends AbstractMBeanMetricCollector<MemoryStatusInfo> {

    private static final Logger log = Logger.getLogger(MemoryMetricCollector.class.getName());

    @Override
    public MemoryStatusInfo collect(final MBeanServerConnection mbsc) {
        try {
            final MemoryMXBean memoryMXBean = ManagementFactory.newPlatformMXBeanProxy(
                    mbsc,
                    ManagementFactory.MEMORY_MXBEAN_NAME,
                    MemoryMXBean.class
            );

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
        } catch (IOException e) {
            log.warning("Failed to get heap memory metric: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

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
