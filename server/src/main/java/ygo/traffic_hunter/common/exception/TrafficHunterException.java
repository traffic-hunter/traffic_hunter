package ygo.traffic_hunter.common.exception;

/**
 * @author yungwang-o
 * @version 1.0.0
 */
public class TrafficHunterException extends RuntimeException {

    public TrafficHunterException() {
    }

    public TrafficHunterException(final String message) {
        super(message);
    }

    public TrafficHunterException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public TrafficHunterException(final Throwable cause) {
        super(cause);
    }

    public TrafficHunterException(final String message, final Throwable cause, final boolean enableSuppression,
                                  final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
