package ygo.traffic_hunter.core.dto.request.systeminfo.gc.collections;

/**
 * @author yungwang-o
 * @version 1.0.0
 */
public record GarbageCollectionTime(
        long getCollectionCount,
        long getCollectionTime
) {
}
