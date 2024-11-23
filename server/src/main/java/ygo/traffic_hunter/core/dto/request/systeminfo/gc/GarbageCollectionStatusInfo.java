package ygo.traffic_hunter.core.dto.request.systeminfo.gc;

import java.util.List;
import ygo.traffic_hunter.core.dto.request.systeminfo.gc.collections.GarbageCollectionTime;

public record GarbageCollectionStatusInfo(List<GarbageCollectionTime> garbageCollectionTimes) {
}
