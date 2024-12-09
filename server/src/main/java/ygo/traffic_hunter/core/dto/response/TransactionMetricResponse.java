package ygo.traffic_hunter.core.dto.response;

import java.time.Instant;
import ygo.traffic_hunter.domain.metric.TransactionData;

/**
 * @author yungwang-o
 * @version 1.0.0
 */
public record TransactionMetricResponse(
        String agentName,
        Instant agentBootTime,
        String agentVersion,
        TransactionData transactionData
) {
}
