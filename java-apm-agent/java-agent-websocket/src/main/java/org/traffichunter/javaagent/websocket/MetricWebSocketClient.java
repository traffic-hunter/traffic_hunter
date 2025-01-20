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
package org.traffichunter.javaagent.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.net.URI;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.traffichunter.javaagent.websocket.converter.SerializationByteArrayConverter;
import org.traffichunter.javaagent.websocket.converter.SerializationByteArrayConverter.MetricType;
import org.traffichunter.javaagent.websocket.metadata.Metadata;

/**
 * The {@code MetricWebSocketClient} class extends {@link WebSocketClient}
 * to provide specialized functionality for sending serialized metrics over a WebSocket connection.
 *
 * <p>Features:</p>
 * <ul>
 *     <li>Automatically sends metadata when the connection is opened.</li>
 *     <li>Supports sending metrics as JSON or compressed binary data.</li>
 *     <li>Provides utility methods to check and manage WebSocket connection state.</li>
 * </ul>
 *
 * @see WebSocketClient
 * @see SerializationByteArrayConverter
 * @see Metadata
 * @see MetricType
 *
 * @author yungwang-o
 * @version 1.0.0
 */
public class MetricWebSocketClient extends WebSocketClient {

    private static final Logger log = LoggerFactory.getLogger(MetricWebSocketClient.class.getName());

    private final SerializationByteArrayConverter converter;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final Metadata metadata;

    public MetricWebSocketClient(final URI serverUri, final Metadata metadata) {
        super(serverUri);
        this.metadata = metadata;
        this.objectMapper
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                .registerModule(new JavaTimeModule());
        this.converter = new SerializationByteArrayConverter(objectMapper);
    }

    @Override
    public void onOpen(final ServerHandshake serverHandshake) {
        log.info("websocket client opened");
        this.toSend(metadata);
    }

    @Override
    public void onMessage(final String s) {
        log.info("websocket client received = {} ", s);
    }

    @Override
    public void onClose(final int i, final String s, final boolean b) {
        log.info("websocket client closed");
    }

    @Override
    public void onError(final Exception e) {
        log.error("websocket client error = {} ", e.getMessage());
    }

    public boolean isConnected() {
        if(isOpen()) {
            return true;
        }

        try {
            return super.connectBlocking();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        return false;
    }

    public boolean isConnected(final long timeOut, final TimeUnit timeUnit) {
        if(isOpen()) {
            return true;
        }

        try {
            return super.connectBlocking(timeOut, timeUnit);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        return false;
    }

    public <M> void toSend(final M metric) {
        if(!isOpen()) {
            throw new IllegalStateException("WebSocket client is closed");
        }

        try {
            String s = objectMapper.writeValueAsString(metric);
            this.send(s);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public <M> void compressToSend(final M metric, final MetricType metricType) {
        if(!isOpen()) {
            throw new IllegalStateException("WebSocket client is closed");
        }

        byte[] transform = converter.transform(metric, metricType);

        this.send(transform);
    }

    public <M> void toSend(final List<M> metrics) {
        if(!isOpen()) {
            throw new IllegalStateException("WebSocket client is closed");
        }

        try {
            this.send(objectMapper.writeValueAsString(metrics));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
