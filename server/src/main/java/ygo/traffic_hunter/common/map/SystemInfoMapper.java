package ygo.traffic_hunter.common.map;

import ygo.traffic_hunter.core.dto.request.metadata.MetadataWrapper;
import ygo.traffic_hunter.core.dto.request.systeminfo.SystemInfo;
import ygo.traffic_hunter.core.dto.response.SystemMetricResponse;
import ygo.traffic_hunter.domain.entity.MetricMeasurement;

/**
 * @author yungwang-o
 * @version 1.0.0
 */
public interface SystemInfoMapper {

    MetricMeasurement map(MetadataWrapper<SystemInfo> wrapper);

    SystemMetricResponse map(MetricMeasurement measurement);
}
