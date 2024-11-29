package ygo.traffic_hunter.core.dto.request.transaction;

import java.time.Instant;
import ygo.traffic_hunter.core.dto.request.Metric;

public record TransactionInfo(
        String txName,
        Instant startTime,
        Instant endTime,
        long duration,
        String errorMessage,
        boolean isSuccess
) {}
