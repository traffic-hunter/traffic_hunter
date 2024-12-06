package ygo.traffic_hunter.core.channel.collector.handler;

import lombok.Getter;

public interface MetricHandler {

    MetricHeaderSpec getHeaderSpec();

    void handle(byte[] payload);

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
