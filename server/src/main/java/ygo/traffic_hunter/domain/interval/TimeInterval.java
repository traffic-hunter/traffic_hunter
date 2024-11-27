package ygo.traffic_hunter.domain.interval;

import lombok.Getter;

@Getter
public enum TimeInterval {

    REAL_TIME("3 seconds"),
    FIVE_MINUTES("5 minutes"),
    TEN_MINUTES("10 minutes"),
    THIRTY_MINUTES("30 minutes"),
    ONE_HOUR("1 hours"),
    THREE_HOURS("3 hours"),
    SIX_HOURS("6 hours"),
    TWELVE_HOURS("12 hours"),
    ONE_DAYS("1 days"),
    TWO_DAYS("2 days"),
    THREE_DAYS("3 days"),
    ;

    private final String interval;

    TimeInterval(final String interval) {
        this.interval = interval;
    }
}
