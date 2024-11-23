package ygo.traffic_hunter.core.dto.request.systeminfo.gc.collections;

public record GarbageCollectionTime(
        long getCollectionCount,
        long getCollectionTime
) {
}
