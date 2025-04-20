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
package ygo.traffic_hunter.core.collector.validator;

import java.util.Objects;
import lombok.RequiredArgsConstructor;
import ygo.traffic_hunter.core.annotation.Validator;
import ygo.traffic_hunter.core.collector.channel.MetricChannel.ChannelException;
import ygo.traffic_hunter.core.dto.request.metadata.AgentMetadata;
import ygo.traffic_hunter.core.dto.request.metadata.AgentStatus;
import ygo.traffic_hunter.core.dto.request.metadata.MetadataWrapper;

/**
 * The {@code MetricValidator} class is responsible for validating the metadata
 * of metrics to ensure their integrity and agent status.
 *
 * @author yungwang-o
 * @version 1.0.0
 */
@Validator
@RequiredArgsConstructor
public class MetricValidator {

    public boolean validate(final MetadataWrapper<?> metric) {
        final AgentMetadata metadata = metric.metadata();

       if (Objects.isNull(metadata)) {
           return true;
       }

       return !isCheck(metadata);
    }

    private boolean isCheck(final AgentMetadata metadata) {
        return Objects.equals(metadata.status(), AgentStatus.RUNNING)
                && !metadata.agentId().isEmpty()
                && !metadata.agentName().isEmpty()
                && !metadata.agentVersion().isEmpty();
    }

    public static final class ChannelValidatedException extends ChannelException {

        public ChannelValidatedException() {
        }

        public ChannelValidatedException(final String message) {
            super(message);
        }

        public ChannelValidatedException(final String message, final Throwable cause) {
            super(message, cause);
        }

        public ChannelValidatedException(final Throwable cause) {
            super(cause);
        }

        public ChannelValidatedException(final String message, final Throwable cause, final boolean enableSuppression,
                                         final boolean writableStackTrace) {
            super(message, cause, enableSuppression, writableStackTrace);
        }
    }
}
