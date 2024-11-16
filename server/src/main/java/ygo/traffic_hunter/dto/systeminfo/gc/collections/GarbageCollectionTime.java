package ygo.traffic_hunter.dto.systeminfo.gc.collections;

public record GarbageCollectionTime(
        long getCollectionCount,
        long getCollectionTime
) {
}
