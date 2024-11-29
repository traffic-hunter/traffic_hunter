package ygo.traffic_hunter.common.map;

import ygo.traffic_hunter.core.dto.request.metadata.MetadataWrapper;
import ygo.traffic_hunter.core.dto.request.transaction.TransactionInfo;
import ygo.traffic_hunter.core.dto.response.TransactionMetricResponse;
import ygo.traffic_hunter.domain.entity.TransactionMeasurement;

public interface TransactionMapper {

    TransactionMeasurement map(MetadataWrapper<TransactionInfo> wrapper);

    TransactionMetricResponse map(TransactionMeasurement measurement);
}
