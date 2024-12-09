package ygo.traffic_hunter.core.collector.channel;

import lombok.Getter;

/**
 * <p>
 *  The {@code MetricChannel} interface defines the contract for handling metric data
 *  based on a specific header specification. Each implementation processes a specific
 *  type of metric payload.
 * </p>
 *
 * <h4>Core Responsibilities</h4>
 * <ul>
 *     <li>Provide a header specification to identify the type of metric handled.</li>
 *     <li>Define the {@code open} method to process incoming payloads.</li>
 * </ul>
 *
 * <h4>Header Specification</h4>
 * <p>The {@code MetricHeaderSpec} enum defines the possible headers used to route
 * metrics to the appropriate {@code MetricChannel} implementation:</p>
 * <ul>
 *     <li>{@code SYSTEM} - Header value: {@code 1}</li>
 *     <li>{@code TRANSACTION} - Header value: {@code 2}</li>
 * </ul>
 *
 * <h4>Implementations</h4>
 * <p>Examples of {@code MetricChannel} implementations:</p>
 * <ul>
 *     <li>{@code SysteminfoMetricChannel} - Handles system metrics.</li>
 *     <li>{@code TransactionMetricChannel} - Handles transaction metrics.</li>
 * </ul>
 *
 * @see MetricHeaderSpec
 *
 * @author yungwang-o
 * @version 1.0.0
 */
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
