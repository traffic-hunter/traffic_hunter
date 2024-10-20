package ygo.traffic_hunter.presentation.response.systeminfo.gc.collections;

public record GarbageCollectionTime(
        long getCollectionCount,
        long getCollectionTime
) {
}
