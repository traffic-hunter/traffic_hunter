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
package org.traffichunter.javaagent.extension.sender.websocket;

import io.opentelemetry.sdk.logs.data.LogRecordData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.traffichunter.javaagent.extension.LogQueue;
import org.traffichunter.javaagent.extension.metadata.AgentMetadata;
import org.traffichunter.javaagent.extension.metadata.MetadataWrapper;
import org.traffichunter.javaagent.extension.sender.MetricSender;
import org.traffichunter.javaagent.websocket.MetricWebSocketClient;
import org.traffichunter.javaagent.websocket.converter.SerializationByteArrayConverter.MetricType;

/**
 * @author yungwang-o
 * @version 1.1.0
 */
public class AgentLogSender implements MetricSender {

    private static final Logger log = LoggerFactory.getLogger(AgentLogSender.class);

    private final MetricWebSocketClient client;

    public AgentLogSender(final MetricWebSocketClient client) {
        this.client = client;
    }

    @Override
    public void toSend(final AgentMetadata metadata) {

        while (!Thread.currentThread().isInterrupted()) {

            try {

                LogRecordData logRecodeData = LogQueue.INSTANCE.poll();

                MetadataWrapper<LogRecordData> metadataWrapper = MetadataWrapper.create(metadata, logRecodeData);

                client.compressToSend(metadataWrapper, MetricType.LOG_METRIC);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (IllegalStateException e) {
                log.warn("exception while sending transaction metric = {}", e.getMessage());
                throw new RuntimeException(e);
            }
        }
    }
}
