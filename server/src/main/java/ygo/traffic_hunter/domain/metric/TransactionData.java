package ygo.traffic_hunter.domain.metric;

import java.time.Instant;

public record TransactionData(

        String txName,

        Instant startTime,

        Instant endTime,

        long duration,

        String errorMessage,

        boolean isSuccess
) {
}
