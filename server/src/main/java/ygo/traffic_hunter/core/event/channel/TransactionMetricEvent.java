package ygo.traffic_hunter.core.event.channel;

import ygo.traffic_hunter.core.dto.request.metadata.MetadataWrapper;
import ygo.traffic_hunter.core.dto.request.transaction.TransactionInfo;

public record TransactionMetricEvent(MetadataWrapper<TransactionInfo> transactionInfo) {
}
