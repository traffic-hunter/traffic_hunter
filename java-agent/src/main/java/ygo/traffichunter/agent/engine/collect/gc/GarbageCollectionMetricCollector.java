package ygo.traffichunter.agent.engine.collect.gc;

import java.io.IOException;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.List;
import java.util.logging.Logger;
import javax.management.MBeanServerConnection;
import ygo.traffichunter.agent.engine.collect.MetricCollector;
import ygo.traffichunter.agent.engine.collect.memory.MemoryMetricCollector;
import ygo.traffichunter.agent.engine.systeminfo.gc.GarbageCollectionStatusInfo;
import ygo.traffichunter.agent.engine.systeminfo.gc.collections.GarbageCollectionTime;

public class GarbageCollectionMetricCollector implements MetricCollector<GarbageCollectionStatusInfo> {

    private static final Logger log = Logger.getLogger(MemoryMetricCollector.class.getName());

    @Override
    public GarbageCollectionStatusInfo collect(final MBeanServerConnection mbsc) {
        try {
            final List<GarbageCollectorMXBean> mxBeans = ManagementFactory.getPlatformMXBeans(mbsc,
                    GarbageCollectorMXBean.class);

            final List<GarbageCollectionTime> garbageCollectionTimes = mxBeans
                    .stream()
                    .map(GarbageCollectionTime::new)
                    .toList();

            return new GarbageCollectionStatusInfo(garbageCollectionTimes);
        } catch (IOException e) {
            log.warning("Failed to get GC metric: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
