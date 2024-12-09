package ygo.traffic_hunter.core.event.channel;

import ygo.traffic_hunter.core.dto.request.metadata.MetadataWrapper;
import ygo.traffic_hunter.core.dto.request.transaction.TransactionInfo;

/**
 * @author yungwang-o
 * @version 1.0.0
 */
public record TransactionMetricEvent(MetadataWrapper<TransactionInfo> transactionInfo) {
}
