package org.traffichunter.javaagent.commons.type;

public enum MetricType {
    SYSTEM_METRIC((byte) 1),
    TRANSACTION_METRIC((byte) 2),
    LOG_METRIC((byte) 3),
    ;

    private final byte value;

    MetricType(final byte value) {
        this.value = value;
    }

    public byte getValue() {
        return value;
    }
}