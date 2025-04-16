/*
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
package ygo.traffic_hunter.core.collector.channel.log;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import ygo.traffic_hunter.core.collector.channel.MetricChannel;
import ygo.traffic_hunter.core.collector.processor.MetricProcessor;
import ygo.traffic_hunter.core.collector.validator.MetricValidator;
import ygo.traffic_hunter.core.collector.validator.MetricValidator.ChannelValidatedException;
import ygo.traffic_hunter.core.dto.request.metadata.MetadataWrapper;
import ygo.traffic_hunter.core.event.channel.LogEvent;
import ygo.traffic_hunter.domain.metric.LogRecord;

/**
 * @author yungwang-o
 * @version 1.1.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LogChannel implements MetricChannel {

    private final MetricProcessor processor;

    private final MetricValidator validator;

    private final ApplicationEventPublisher publisher;

    @Override
    public MetricHeaderSpec getHeaderSpec() {
        return MetricHeaderSpec.LOG;
    }

    @Override
    public void open(final byte[] payload) {

        MetadataWrapper<LogRecord> data = processor.process(payload, LogRecord.class);

        log.info("log info: {}", data);

        if(validator.validate(data)) {
            throw new ChannelValidatedException("The input does not meet the required validation criteria.");
        }

        publisher.publishEvent(new LogEvent(data));
    }
}
