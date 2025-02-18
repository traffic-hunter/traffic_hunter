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
package ygo.traffic_hunter.core.event.channel;

import static ygo.traffic_hunter.config.cache.CacheConfig.CacheType.ALARM_CACHE_NAME;

import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ygo.traffic_hunter.common.map.SystemInfoMapper;
import ygo.traffic_hunter.common.map.TransactionMapper;
import ygo.traffic_hunter.core.alarm.AlarmManager;
import ygo.traffic_hunter.core.collector.validator.MetricValidator;
import ygo.traffic_hunter.core.dto.request.metadata.MetadataWrapper;
import ygo.traffic_hunter.core.dto.request.systeminfo.SystemInfo;
import ygo.traffic_hunter.core.dto.request.transaction.TransactionInfo;
import ygo.traffic_hunter.core.dto.response.alarm.ThresholdResponse;
import ygo.traffic_hunter.core.repository.MetricRepository;
import ygo.traffic_hunter.core.service.AlarmService;
import ygo.traffic_hunter.core.webhook.message.MessageType;
import ygo.traffic_hunter.domain.entity.MetricMeasurement;
import ygo.traffic_hunter.domain.entity.TransactionMeasurement;
import ygo.traffic_hunter.domain.entity.alarm.Threshold.CalculatedThreshold;
import ygo.traffic_hunter.domain.entity.alarm.Threshold.Calculator;
import ygo.traffic_hunter.domain.metric.TraceInfo;

/**
 * <p>
 * The {@code ChannelEventHandler} class is responsible for handling metric events and persisting the processed data
 * into a repository. It ensures transaction management and validation of incoming metric events.
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
 * @author yungwang-o
 * @version 1.0.0
 * @see TransactionMetricEvent
 * @see SystemInfoMetricEvent
 * @see MetricValidator
 * @see MetricRepository
 */
@Component
@RequiredArgsConstructor
public class ChannelEventHandler {

    private final SystemInfoMapper systemInfoMapper;

    private final TransactionMapper transactionMapper;

    private final MetricRepository metricRepository;

    private final AlarmService alarmService;

    private final AlarmManager alarmManager;

    private final CacheManager cacheManager;

    @EventListener
    @Transactional
    public void handle(final TransactionMetricEvent event) {

        MetadataWrapper<TraceInfo> object = event.transactionInfo();

        TransactionMeasurement measurement = transactionMapper.map(object);

        metricRepository.save(measurement);
    }

    @EventListener
    @Transactional
    public void handle(final SystemInfoMetricEvent event) {

        MetadataWrapper<SystemInfo> object = event.systemInfo();

        MetricMeasurement measurement = systemInfoMapper.map(object);

        metricRepository.save(measurement);
    }

    @EventListener
    public void handle(final AlarmEvent event) {
        MetadataWrapper<SystemInfo> object = event.systemInfo();
        ThresholdResponse threshold = alarmService.retrieveThreshold();
        Calculator calculator = Calculator.builder()
                .memoryStatusInfo(object.data().memoryStatusInfo())
                .cpuStatusInfo(object.data().cpuStatusInfo())
                .threadStatusInfo(object.data().threadStatusInfo())
                .tomcatWebServerInfo(object.data().tomcatWebServerInfo())
                .hikariDbcpInfo(object.data().hikariDbcpInfo())
                .build();

        CalculatedThreshold calculate = calculator.calculate(threshold);

        // CPU Alarm
        if (canSend(MessageType.CPU, object.data().cpuStatusInfo().processCpuLoad(), calculate.calculateCpu())) {
            alarmManager.send(MessageType.CPU.doMessage(null, object));
            Objects.requireNonNull(cacheManager.getCache(ALARM_CACHE_NAME)).put(MessageType.CPU.name(), true);
        }

        // Memory Alarm
        if (canSend(MessageType.MEMORY, object.data().memoryStatusInfo().heapMemoryUsage().used(),
                calculate.calculateMemory())) {
            alarmManager.send(MessageType.MEMORY.doMessage(null, object));
            Objects.requireNonNull(cacheManager.getCache(ALARM_CACHE_NAME)).put(MessageType.MEMORY.name(), true);
        }

        // Thread Alarm
        if (canSend(MessageType.THREAD, object.data().threadStatusInfo().threadCount(),
                calculate.calculateThread())) {
            alarmManager.send(MessageType.THREAD.doMessage(null, object));
            Objects.requireNonNull(cacheManager.getCache(ALARM_CACHE_NAME)).put(MessageType.THREAD.name(), true);
        }

        // Web Request Alarm
        if (canSend(MessageType.WEB_REQUEST, object.data().tomcatWebServerInfo().tomcatRequestInfo().requestCount(),
                calculate.calculateWebRequest())) {
            alarmManager.send(MessageType.WEB_REQUEST.doMessage(null, object));
            Objects.requireNonNull(cacheManager.getCache(ALARM_CACHE_NAME)).put(MessageType.WEB_REQUEST.name(), true);
        }

        // Web Thread Alarm
        if (canSend(MessageType.WEB_THREAD, object.data().threadStatusInfo().threadCount(),
                calculate.calculateWebThread())) {
            alarmManager.send(MessageType.WEB_THREAD.doMessage(null, object));
            Objects.requireNonNull(cacheManager.getCache(ALARM_CACHE_NAME)).put(MessageType.WEB_THREAD.name(), true);
        }

        // DBCP Alarm
        if (canSend(MessageType.DBCP, object.data().hikariDbcpInfo().activeConnections(),
                calculate.calculateDbcp())) {
            alarmManager.send(MessageType.DBCP.doMessage(null, object));
            Objects.requireNonNull(cacheManager.getCache(ALARM_CACHE_NAME)).put(MessageType.DBCP.name(), true);
        }
    }

    private boolean canSend(MessageType messageType, double currentValue, double thresholdValue) {
        Cache cache = cacheManager.getCache(ALARM_CACHE_NAME);
        if (cache != null && cache.get(messageType.name()) != null) {
            return false;
        }
        return thresholdValue <= currentValue;
    }
}
