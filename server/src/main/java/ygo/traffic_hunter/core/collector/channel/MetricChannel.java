/**
 * The MIT License
 *
 * Copyright (c) 2024 traffic-hunter.org
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
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

    class ChannelException extends RuntimeException {

        public ChannelException() {
        }

        public ChannelException(final String message) {
            super(message);
        }

        public ChannelException(final String message, final Throwable cause) {
            super(message, cause);
        }

        public ChannelException(final Throwable cause) {
            super(cause);
        }

        public ChannelException(final String message, final Throwable cause, final boolean enableSuppression,
                                final boolean writableStackTrace) {
            super(message, cause, enableSuppression, writableStackTrace);
        }
    }
}
