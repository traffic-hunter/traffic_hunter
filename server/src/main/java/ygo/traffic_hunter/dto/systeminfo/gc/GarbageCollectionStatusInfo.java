package ygo.traffic_hunter.dto.systeminfo.gc;

import java.util.List;
import ygo.traffic_hunter.dto.systeminfo.gc.collections.GarbageCollectionTime;

public record GarbageCollectionStatusInfo(List<GarbageCollectionTime> garbageCollectionTimes) {
}
