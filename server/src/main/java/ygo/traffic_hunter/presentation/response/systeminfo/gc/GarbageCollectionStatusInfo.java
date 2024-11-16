package ygo.traffic_hunter.presentation.response.systeminfo.gc;

import java.util.List;
import ygo.traffic_hunter.presentation.response.systeminfo.gc.collections.GarbageCollectionTime;

public record GarbageCollectionStatusInfo(List<GarbageCollectionTime> garbageCollectionTimes) {
}
