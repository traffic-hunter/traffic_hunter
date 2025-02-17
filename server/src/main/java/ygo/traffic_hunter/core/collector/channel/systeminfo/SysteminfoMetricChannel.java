/**
 * The MIT License
 * <p>
 * Copyright (c) 2024 yungwang-o
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the
 * Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package ygo.traffic_hunter.core.collector.channel.systeminfo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import ygo.traffic_hunter.core.collector.channel.MetricChannel;
import ygo.traffic_hunter.core.collector.processor.MetricProcessor;
import ygo.traffic_hunter.core.collector.validator.MetricValidator;
import ygo.traffic_hunter.core.collector.validator.MetricValidator.ChannelValidatedException;
import ygo.traffic_hunter.core.dto.request.metadata.MetadataWrapper;
import ygo.traffic_hunter.core.dto.request.systeminfo.SystemInfo;
import ygo.traffic_hunter.core.event.channel.AlarmEvent;
import ygo.traffic_hunter.core.event.channel.ChannelEventHandler;
import ygo.traffic_hunter.core.event.channel.SystemInfoMetricEvent;

/**
 * The {@code SysteminfoMetricChannel} class processes system metric payloads and publishes events for further handling
 * in the application.
 *
 * @author yungwang-o
 * @version 1.0.0
 * @see MetricProcessor
 * @see ChannelEventHandler
 * @see SystemInfoMetricEvent
 * @see MetricChannel
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SysteminfoMetricChannel implements MetricChannel {

    private final MetricProcessor processor;

    private final MetricValidator validator;

    private final ApplicationEventPublisher publisher;

    @Override
    public MetricHeaderSpec getHeaderSpec() {
        return MetricHeaderSpec.SYSTEM;
    }

    @Override
    public void open(final byte[] payload) {

        MetadataWrapper<SystemInfo> object = processor.process(payload, SystemInfo.class);

        log.info("process system info: {}", object);

        if (validator.validate(object)) {
            throw new ChannelValidatedException("The input does not meet the required validation criteria.");
        }

        publisher.publishEvent(new SystemInfoMetricEvent(object));
        publisher.publishEvent(new AlarmEvent(object));
    }
}
