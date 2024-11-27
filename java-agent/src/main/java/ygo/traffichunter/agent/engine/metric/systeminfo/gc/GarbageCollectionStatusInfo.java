package ygo.traffichunter.agent.engine.metric.systeminfo.gc;

import java.util.List;
import ygo.traffichunter.agent.engine.metric.systeminfo.gc.collections.GarbageCollectionTime;

public record GarbageCollectionStatusInfo(List<GarbageCollectionTime> garbageCollectionTimes) {
}
