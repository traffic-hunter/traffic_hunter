/**
 * The MIT License
 *
 * Copyright (c) 2024 yungwang-o
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
package ygo.traffic_hunter.core.event.channel;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ygo.traffic_hunter.common.map.SystemInfoMapper;
import ygo.traffic_hunter.common.map.TransactionMapper;
import ygo.traffic_hunter.core.collector.validator.MetricValidator;
import ygo.traffic_hunter.core.dto.request.metadata.MetadataWrapper;
import ygo.traffic_hunter.core.dto.request.systeminfo.SystemInfo;
import ygo.traffic_hunter.core.dto.request.transaction.TransactionInfo;
import ygo.traffic_hunter.core.repository.MetricRepository;
import ygo.traffic_hunter.domain.entity.MetricMeasurement;
import ygo.traffic_hunter.domain.entity.TransactionMeasurement;
import ygo.traffic_hunter.domain.metric.TraceInfo;

/**
 * <p>
 *  The {@code ChannelEventHandler} class is responsible for handling metric events and
 *  persisting the processed data into a repository. It ensures transaction management
 *  and validation of incoming metric events.
 * </p>
 *
 * <h4>Core Responsibilities</h4>
 * <ul>
 *     <li>Listens for {@link TransactionMetricEvent} and {@link SystemInfoMetricEvent} events.</li>
 *     <li>Validates incoming metrics using {@link MetricValidator}.</li>
 *     <li>Maps valid metric events to database entities using mappers.</li>
 *     <li>Persists the mapped entities into the {@link MetricRepository}.</li>
 *     <li>Handles transactions independently for each event.</li>
 * </ul>
 *
 * <h4>Transaction Management</h4>
 * <p>
 * Each event listener method is annotated with {@code @Transactional}, ensuring that:
 * </p>
 * <ul>
 *     <li>Each event is processed within its own transaction.</li>
 *     <li>Failures in processing one event do not affect the processing of others.</li>
 * </ul>
 *
 * <h2>Workflow</h2>
 * <ol>
 *     <li>An event is published ({@link TransactionMetricEvent} or {@link SystemInfoMetricEvent}).</li>
 *     <li>The corresponding event handler method is triggered.</li>
 *     <li>The metric data is validated using {@link MetricValidator}.</li>
 *     <li>If valid, the metric is mapped to a database entity.</li>
 *     <li>The entity is saved in the database.</li>
 * </ol>
 *
 * <h2>Dependencies</h2>
 * <ul>
 *     <li>{@link MetricValidator}: Ensures metrics meet validation criteria.</li>
 *     <li>{@link SystemInfoMapper}: Maps {@link SystemInfo} metrics to database entities.</li>
 *     <li>{@link TransactionMapper}: Maps {@link TransactionInfo} metrics to database entities.</li>
 *     <li>{@link MetricRepository}: Handles database persistence of metrics.</li>
 * </ul>
 *
 * @see TransactionMetricEvent
 * @see SystemInfoMetricEvent
 * @see MetricValidator
 * @see MetricRepository
 *
 * @author yungwang-o
 * @version 1.0.0
 */
@Component
@RequiredArgsConstructor
public class ChannelEventHandler {

    private final SystemInfoMapper systemInfoMapper;

    private final TransactionMapper transactionMapper;

    private final MetricValidator validator;

    private final MetricRepository repository;

    @EventListener
    @Transactional
    public void handle(final TransactionMetricEvent event) {

        MetadataWrapper<TraceInfo> object = event.transactionInfo();

        if(validator.validate(object)) {
            return;
        }

        TransactionMeasurement measurement = transactionMapper.map(object);

        repository.save(measurement);
    }

    @EventListener
    @Transactional
    public void handle(final SystemInfoMetricEvent event) {

        MetadataWrapper<SystemInfo> object = event.systemInfo();

        if(validator.validate(object)) {
            return;
        }

        MetricMeasurement measurement = systemInfoMapper.map(object);

        repository.save(measurement);
    }
}
