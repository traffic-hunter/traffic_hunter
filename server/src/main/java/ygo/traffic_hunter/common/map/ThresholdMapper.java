package ygo.traffic_hunter.common.map;

import ygo.traffic_hunter.core.dto.request.metadata.MetadataWrapper;
import ygo.traffic_hunter.core.dto.request.systeminfo.SystemInfo;
import ygo.traffic_hunter.domain.entity.alarm.Threshold;

public interface ThresholdMapper {

    Threshold map(MetadataWrapper<SystemInfo> wrapper);
}
