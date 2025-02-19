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
package ygo.traffic_hunter.core.collector.channel.transaction;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import ygo.traffic_hunter.core.collector.channel.MetricChannel;
import ygo.traffic_hunter.core.collector.processor.MetricProcessor;
import ygo.traffic_hunter.core.collector.validator.MetricValidator;
import ygo.traffic_hunter.core.collector.validator.MetricValidator.ChannelValidatedException;
import ygo.traffic_hunter.core.dto.request.metadata.MetadataWrapper;
import ygo.traffic_hunter.core.event.channel.ChannelEventHandler;
import ygo.traffic_hunter.core.event.channel.TransactionMetricEvent;
import ygo.traffic_hunter.domain.metric.TraceInfo;

/**
 * The {@code TransactionMetricChannel} class handles transaction metrics by processing
 * the raw payload and publishing events for further processing.
 *
 * @see MetricProcessor
 * @see ChannelEventHandler
 * @see TransactionMetricEvent
 * @see MetricChannel
 *
 * @author yungwang-o
 * @version 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TransactionMetricChannel implements MetricChannel {

    private final MetricProcessor processor;

    private final MetricValidator validator;

    private final ApplicationEventPublisher publisher;

    @Override
    public MetricHeaderSpec getHeaderSpec() {
        return MetricHeaderSpec.TRANSACTION;
    }

    @Override
    public void open(final byte[] payload) {

        MetadataWrapper<TraceInfo> object = processor.process(payload, TraceInfo.class);

        log.info("Transaction metric data: {}", object);

        if(validator.validate(object)) {
            throw new ChannelValidatedException("The input does not meet the required validation criteria.");
        }

        publisher.publishEvent(new TransactionMetricEvent(object));
    }
}
