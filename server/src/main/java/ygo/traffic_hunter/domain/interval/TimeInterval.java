package ygo.traffic_hunter.domain.interval;

import lombok.Getter;

@Getter
public enum TimeInterval {

    TEN_MINUTES("10 minutes"),
    ;

    private final String interval;

    TimeInterval(final String interval) {
        this.interval = interval;
    }
}
