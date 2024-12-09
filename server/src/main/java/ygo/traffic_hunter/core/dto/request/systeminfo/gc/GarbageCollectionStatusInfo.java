package ygo.traffic_hunter.core.dto.request.systeminfo.gc;

import java.util.List;
import ygo.traffic_hunter.core.dto.request.systeminfo.gc.collections.GarbageCollectionTime;

/**
 * @author yungwang-o
 * @version 1.0.0
 */
public record GarbageCollectionStatusInfo(List<GarbageCollectionTime> garbageCollectionTimes) {
}
