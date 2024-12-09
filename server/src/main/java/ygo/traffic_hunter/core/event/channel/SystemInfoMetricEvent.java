package ygo.traffic_hunter.core.event.channel;

import ygo.traffic_hunter.core.dto.request.metadata.MetadataWrapper;
import ygo.traffic_hunter.core.dto.request.systeminfo.SystemInfo;

/**
 * @author yungwang-o
 * @version 1.0.0
 */
public record SystemInfoMetricEvent(MetadataWrapper<SystemInfo> systemInfo) {
}
