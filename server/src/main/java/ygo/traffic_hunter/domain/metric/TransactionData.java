package ygo.traffic_hunter.domain.metric;

import java.time.Instant;

/**
 * @author yungwang-o
 * @version 1.0.0
 */
public record TransactionData(

        String txName,

        Instant startTime,

        Instant endTime,

        long duration,

        String errorMessage,

        boolean isSuccess
) {
}
