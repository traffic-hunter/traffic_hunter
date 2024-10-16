package ygo.traffichunter.agent.engine.systeminfo.gc;

import java.util.List;
import ygo.traffichunter.agent.engine.systeminfo.gc.collections.GarbageCollectionTime;

public record GarbageCollectionStatusInfo(List<GarbageCollectionTime> garbageCollectionTimes) {
}
