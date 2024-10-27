package ygo.traffichunter.agent.engine.instrument.collect;

import java.time.Instant;

public record TransactionMetric(
        String txName,
        Instant startTime,
        Instant endTime,
        long duration,
        String errorMessage,
        boolean isSuccess
) {
}
