package ygo.traffic_hunter.core.dto.response;

import java.time.Instant;
import ygo.traffic_hunter.domain.metric.TransactionData;

public record TransactionMetricResponse(
        String agentName,
        Instant agentBootTime,
        String agentVersion,
        TransactionData transactionData
) {
}
