package ygo.traffic_hunter.dto.systeminfo;

import java.time.Instant;

public record TransactionInfo(
        String txName,
        Instant startTime,
        Instant endTime,
        long duration,
        String errorMessage,
        boolean isSuccess
) {
}
