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
package org.traffichunter.javaagent.extension.sender.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.traffichunter.javaagent.extension.metadata.AgentMetadata;
import org.traffichunter.javaagent.extension.metadata.MetadataWrapper;
import org.traffichunter.javaagent.extension.TraceInfo;
import org.traffichunter.javaagent.extension.TraceQueue;
import org.traffichunter.javaagent.extension.sender.MetricSender;
import org.traffichunter.javaagent.websocket.MetricWebSocketClient;
import org.traffichunter.javaagent.websocket.converter.SerializationByteArrayConverter.MetricType;

/**
 * <p>
 * The {@code AgentTransactionMetricSender} class is responsible for sending transaction metrics
 * to the server via a WebSocket connection.
 * </p>
 *
 * <p>Features:</p>
 * <ul>
 *     <li>Continuously retrieves transaction data from a synchronized queue.</li>
 *     <li>Wraps the transaction data with metadata and sends it in a compressed format.</li>
 *     <li>Relies on {@link TraceQueue} for thread-safe access to transaction data.</li>
 * </ul>
 *
 * @see MetricSender
 * @see MetricWebSocketClient
 * @see TraceQueue
 *
 * @author yungwang-o
 * @version 1.0.0
 */
public class AgentTransactionMetricSender implements MetricSender {

    public static final Logger log = LoggerFactory.getLogger(AgentTransactionMetricSender.class);

    private final MetricWebSocketClient client;

    public AgentTransactionMetricSender(final MetricWebSocketClient client) {
        this.client = client;
    }

    @Override
    public void toSend(final AgentMetadata metadata)  {

        while (!Thread.currentThread().isInterrupted()) {

            try {

                TraceInfo trInfo = TraceQueue.INSTANCE.poll();

                MetadataWrapper<TraceInfo> wrapper = MetadataWrapper.create(metadata, trInfo);

                client.compressToSend(wrapper, MetricType.TRANSACTION_METRIC);
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
