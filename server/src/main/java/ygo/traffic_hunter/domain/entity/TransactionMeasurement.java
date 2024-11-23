package ygo.traffic_hunter.domain.entity;

import java.time.Instant;
import ygo.traffic_hunter.domain.metric.TransactionData;

public record TransactionMeasurement(

        Instant time,

        String agentId,

        String agentName,

        String agentVersion,

        Instant agentBootTime,

        TransactionData transactionData
) {
}
