package ygo.traffichunter.agent.engine.metric.transaction;

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
) {

    public static TransactionInfo create(final String txName,
                                         final Instant startTime,
                                         final Instant endTime,
                                         final long duration,
                                         final String errorMessage,
                                         final boolean isSuccess) {

        return new TransactionInfo(txName, startTime, endTime, duration, errorMessage, isSuccess);
    }
}
