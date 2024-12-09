package ygo.traffic_hunter.domain.entity;

import java.time.Instant;
import ygo.traffic_hunter.domain.metric.TransactionData;

/**
 * @author yungwang-o
 * @version 1.0.0
 */
public record TransactionMeasurement(

        Instant time,

        Integer agentId,

        TransactionData transactionData
) {
}
