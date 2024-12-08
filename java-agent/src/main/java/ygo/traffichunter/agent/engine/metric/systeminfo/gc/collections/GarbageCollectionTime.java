package ygo.traffichunter.agent.engine.metric.systeminfo.gc.collections;

import java.lang.management.GarbageCollectorMXBean;

/**
 * @author yungwang-o
 * @version 1.0.0
 */
public record GarbageCollectionTime(
        long getCollectionCount,
        long getCollectionTime
) {

    public GarbageCollectionTime(final GarbageCollectorMXBean mxBean) {
        this(mxBean.getCollectionCount(), mxBean.getCollectionTime());
    }
}
