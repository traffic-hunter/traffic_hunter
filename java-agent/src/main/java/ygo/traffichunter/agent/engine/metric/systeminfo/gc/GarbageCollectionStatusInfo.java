package ygo.traffichunter.agent.engine.metric.systeminfo.gc;

import java.util.List;
import ygo.traffichunter.agent.engine.metric.systeminfo.gc.collections.GarbageCollectionTime;

/**
 * @author yungwang-o
 * @version 1.0.0
 */
public record GarbageCollectionStatusInfo(List<GarbageCollectionTime> garbageCollectionTimes) {
}
