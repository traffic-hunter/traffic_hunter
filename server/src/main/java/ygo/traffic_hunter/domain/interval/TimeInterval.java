package ygo.traffic_hunter.domain.interval;

import lombok.Getter;

/**
 * @author yungwang-o
 * @version 1.0.0
 */
@Getter
public enum TimeInterval {

    REAL_TIME("3 seconds", 5_000),
    FIVE_MINUTES("5 minutes", 5_000),
    TEN_MINUTES("10 minutes", 5_000),
    THIRTY_MINUTES("30 minutes", 8_000),
    ONE_HOUR("1 hours", 10_000),
    THREE_HOURS("3 hours", 10_000),
    SIX_HOURS("6 hours", 30_000),
    TWELVE_HOURS("12 hours", 30_000),
    ONE_DAYS("1 days", 60_000),
    TWO_DAYS("2 days", 60_000),
    THREE_DAYS("3 days", 60_000),
    ;

    private final String interval;
    private final long delayMillis;

    TimeInterval(final String interval, final long delayMillis) {
        this.interval = interval;
        this.delayMillis = delayMillis;
    }
}
