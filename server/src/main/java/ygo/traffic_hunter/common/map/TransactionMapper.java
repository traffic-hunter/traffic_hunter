package ygo.traffic_hunter.common.map;

import ygo.traffic_hunter.core.dto.request.metadata.MetadataWrapper;
import ygo.traffic_hunter.core.dto.response.TransactionMetricResponse;
import ygo.traffic_hunter.domain.entity.TransactionMeasurement;
import ygo.traffic_hunter.domain.metric.TraceInfo;

/**
 * @author yungwang-o
 * @version 1.0.0
 */
public interface TransactionMapper {

    TransactionMeasurement map(MetadataWrapper<TraceInfo> wrapper);

    TransactionMetricResponse map(TransactionMeasurement measurement);
}
