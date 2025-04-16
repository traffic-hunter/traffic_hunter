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
package org.traffichunter.javaagent.extension.exporter.thunter;

import io.opentelemetry.sdk.common.CompletableResultCode;
import io.opentelemetry.sdk.logs.data.LogRecordData;
import io.opentelemetry.sdk.logs.export.LogRecordExporter;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;
import org.traffichunter.javaagent.bootstrap.Configurations;
import org.traffichunter.javaagent.bootstrap.Configurations.ConfigProperty;
import org.traffichunter.javaagent.commons.type.MetricType;
import org.traffichunter.javaagent.extension.LogRecord;
import org.traffichunter.javaagent.extension.metadata.AgentMetadata;
import org.traffichunter.javaagent.extension.metadata.MetadataWrapper;
import org.traffichunter.javaagent.websocket.TrafficHunterWebsocketClient;

/**
 * @author yungwang-o
 * @version 1.1.0
 */
public final class TrafficHunterLogExporter implements LogRecordExporter {

    private static final Logger log = Logger.getLogger(TrafficHunterLogExporter.class.getName());

    private static final Boolean exporterLogging = Configurations.debug(ConfigProperty.EXPORTER_DEBUG);

    private final AtomicBoolean isShutdown = new AtomicBoolean();

    private final TrafficHunterWebsocketClient client;

    private final AgentMetadata metadata;

    public TrafficHunterLogExporter(final TrafficHunterWebsocketClient client,
                                    final AgentMetadata metadata) {
        this.client = client;
        this.metadata = metadata;
    }

    @Override
    public CompletableResultCode export(final Collection<LogRecordData> collection) {

        if(isShutdown.get()) {
            return CompletableResultCode.ofFailure();
        }

        if(exporterLogging) {
            log.info("log exporting = " + collection);
        }

        collection.stream()
                .map(TrafficHunterLogExporter::mapToLogRecord)
                .map(logRecord -> MetadataWrapper.create(metadata, logRecord))
                .forEach(logRecord -> client.toSend(logRecord, MetricType.LOG_METRIC));

        return CompletableResultCode.ofSuccess();
    }

    @Override
    public CompletableResultCode flush() {
        return CompletableResultCode.ofSuccess();
    }

    @Override
    public CompletableResultCode shutdown() {

        if(!isShutdown.compareAndSet(false, true)) {
            return CompletableResultCode.ofSuccess();
        }

        client.close();

        return CompletableResultCode.ofSuccess();
    }

    private static LogRecord mapToLogRecord(final LogRecordData data) {
        return LogRecord.builder()
                .severity(data.getSeverity())
                .attributes(data.getAttributes())
                .instrumentationScopeInfo(data.getInstrumentationScopeInfo())
                .resource(data.getResource())
                .observedTimestampEpochNanos(data.getObservedTimestampEpochNanos())
                .severityText(data.getSeverityText())
                .timestampEpochNanos(data.getTimestampEpochNanos())
                .totalAttributeCount(data.getTotalAttributeCount())
                .build();
    }
}
