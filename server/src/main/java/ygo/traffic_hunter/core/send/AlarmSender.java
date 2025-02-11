package ygo.traffic_hunter.core.send;

import ygo.traffic_hunter.core.webhook.message.Message;

public interface AlarmSender {

    void send(Message message);

    class AlarmException extends RuntimeException {

        public AlarmException() {
            super();
        }

        public AlarmException(final String message) {
            super(message);
        }

        public AlarmException(final String message, final Throwable cause) {
            super(message, cause);
        }

        public AlarmException(final Throwable cause) {
            super(cause);
        }

        protected AlarmException(final String message, final Throwable cause, final boolean enableSuppression,
                                 final boolean writableStackTrace) {
            super(message, cause, enableSuppression, writableStackTrace);
        }
    }
}
