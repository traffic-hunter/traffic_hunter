package ygo.traffic_hunter.core.event.channel;

import ygo.traffic_hunter.core.dto.request.metadata.MetadataWrapper;
import ygo.traffic_hunter.domain.metric.TraceInfo;

/**
 * @author yungwang-o
 * @version 1.0.0
 */
public record TransactionMetricEvent(MetadataWrapper<TraceInfo> transactionInfo) {
}
