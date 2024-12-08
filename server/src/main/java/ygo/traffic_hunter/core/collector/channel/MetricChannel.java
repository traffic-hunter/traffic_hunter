package ygo.traffic_hunter.core.collector.channel;

import lombok.Getter;

public interface MetricChannel {

    MetricHeaderSpec getHeaderSpec();

    void open(byte[] payload);

    @Getter
    enum MetricHeaderSpec {
        SYSTEM((byte) 1),
        TRANSACTION((byte) 2),
        ;

        private final byte header;

        MetricHeaderSpec(final byte header) {
            this.header = header;
        }

        public boolean equals(final byte header) {
            return this.header == header;
        }
    }
}
