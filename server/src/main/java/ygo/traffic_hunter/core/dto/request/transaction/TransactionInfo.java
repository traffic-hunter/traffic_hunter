package ygo.traffic_hunter.core.dto.request.transaction;

import java.time.Instant;

/**
 * @author yungwang-o
 * @version 1.0.0
 */
public record TransactionInfo(
        String txName,
        Instant startTime,
        Instant endTime,
        long duration,
        String errorMessage,
        boolean isSuccess
) {}
