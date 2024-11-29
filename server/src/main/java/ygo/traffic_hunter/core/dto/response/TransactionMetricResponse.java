package ygo.traffic_hunter.core.dto.response;

import ygo.traffic_hunter.domain.metric.TransactionData;

public record TransactionMetricResponse(String agentName, TransactionData transactionData) {
}
