package ygo.traffic_hunter.core.event.channel;

import ygo.traffic_hunter.core.dto.request.metadata.MetadataWrapper;
import ygo.traffic_hunter.core.dto.request.systeminfo.SystemInfo;

public record SystemInfoMetricEvent(MetadataWrapper<SystemInfo> systemInfo) {
}
