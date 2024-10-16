package ygo.traffichunter.agent.engine.systeminfo.gc.collections;

import java.lang.management.GarbageCollectorMXBean;

public record GarbageCollectionTime(
        long getCollectionCount,
        long getCollectionTime
) {

    public GarbageCollectionTime(final GarbageCollectorMXBean mxBean) {
        this(mxBean.getCollectionCount(), mxBean.getCollectionTime());
    }
}
