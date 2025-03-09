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

import java.util.logging.Logger;
import org.traffichunter.javaagent.extension.bootstrap.property.TrafficHunterAgentProperty;
import org.traffichunter.javaagent.extension.metadata.AgentMetadata;
import org.traffichunter.javaagent.extension.metadata.MetadataWrapper;
import org.traffichunter.javaagent.extension.sender.MetricSender;
import org.traffichunter.javaagent.jmx.collect.MetricCollectSupport;
import org.traffichunter.javaagent.jmx.metric.systeminfo.SystemInfo;
import org.traffichunter.javaagent.websocket.MetricWebSocketClient;
import org.traffichunter.javaagent.websocket.converter.SerializationByteArrayConverter.MetricType;

/**
 * The {@code AgentSystemMetricSender} class is responsible for sending system metrics
 * to the server via a WebSocket connection.
 *
 * <p>Features:</p>
 * <ul>
 *     <li>Collects system metric data from the local environment.</li>
 *     <li>Wraps the system data with metadata and sends it in a compressed format.</li>
 *     <li>Uses {@link MetricCollectSupport} for metric collection.</li>
 * </ul>
 *
 * @see MetricSender
 * @see MetricWebSocketClient
 * @see MetricCollectSupport
 *
 * @author yungwang-o
 * @version 1.0.0
 */
public class AgentSystemMetricSender implements MetricSender {

    private static final Logger log = Logger.getLogger(AgentSystemMetricSender.class.getName());

    private final MetricWebSocketClient client;

    private final MetricCollectSupport metricCollectSupport;

    public AgentSystemMetricSender(final MetricWebSocketClient client,
                                   final TrafficHunterAgentProperty property) {
        this.client = client;
        this.metricCollectSupport = new MetricCollectSupport(property.targetUri());
    }

    @Override
    public void toSend(final AgentMetadata metadata) {
        final SystemInfo systemInfo = metricCollectSupport.collect();

        final MetadataWrapper<SystemInfo> wrapper = MetadataWrapper.create(metadata, systemInfo);

        client.compressToSend(wrapper, MetricType.SYSTEM_METRIC);
    }
}
