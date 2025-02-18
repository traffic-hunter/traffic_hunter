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
package ygo.traffic_hunter.core.alarm.message;

import lombok.Getter;
import ygo.traffic_hunter.core.dto.request.metadata.MetadataWrapper;
import ygo.traffic_hunter.core.dto.request.systeminfo.SystemInfo;
import ygo.traffic_hunter.core.alarm.message.library.MessageMaker;
import ygo.traffic_hunter.core.alarm.message.library.MessageMaker.Color;

/**
 * @author yungwang-o
 * @version 1.1.0
 */
@Getter
public enum MessageType {

    CPU("Cpu alert!!", "[[ Alert ]] !!", "CPU usage has exceeded the threshold: {}% !!") {
        @Override
        public Message doMessage(final String url, final MetadataWrapper<SystemInfo> metadataWrapper) {

            return MessageMaker.builder()
                    .url(url)
                    .title(MessageType.CPU.getTitle())
                    .content(MessageType.CPU.getContent())
                    .color(Color.RED)
                    .agentName(metadataWrapper.metadata().agentName())
                    .times(metadataWrapper.metadata().startTime())
                    .description(MessageType.CPU.getBody(), metadataWrapper.data().cpuStatusInfo().processCpuLoad())
                    .build();
        }
    },
    MEMORY("Memory alert!!", "[[ Alert ]] !!", "Memory usage has exceeded the threshold: {} !!") {
        @Override
        public Message doMessage(final String url, final MetadataWrapper<SystemInfo> metadataWrapper) {

            String used = String.format("%.1f", (double) metadataWrapper.data()
                    .memoryStatusInfo()
                    .heapMemoryUsage()
                    .used() / (1024 * 1024));

            return MessageMaker.builder()
                    .url(url)
                    .title(MessageType.MEMORY.getTitle())
                    .content(MessageType.MEMORY.getContent())
                    .color(Color.RED)
                    .agentName(metadataWrapper.metadata().agentName())
                    .times(metadataWrapper.metadata().startTime())
                    .description(MessageType.MEMORY.getBody(), used)
                    .build();
        }
    },
    THREAD("Thread alert!!", "[[ Alert ]] !!", "Thread usage has exceeded the threshold: {} !!") {
        @Override
        public Message doMessage(final String url, final MetadataWrapper<SystemInfo> metadataWrapper) {

            return MessageMaker.builder()
                    .url(url)
                    .title(MessageType.THREAD.getTitle())
                    .content(MessageType.THREAD.getContent())
                    .color(Color.RED)
                    .agentName(metadataWrapper.metadata().agentName())
                    .times(metadataWrapper.metadata().startTime())
                    .description(MessageType.THREAD.getBody(), metadataWrapper.data().threadStatusInfo().threadCount())
                    .build();
        }
    },
    WEB_REQUEST("Web request alert!!", "[[ Alert ]] !!", "Web request usage has exceeded the threshold: {} !!") {
        @Override
        public Message doMessage(final String url, final MetadataWrapper<SystemInfo> metadataWrapper) {

            return MessageMaker.builder()
                    .url(url)
                    .title(MessageType.WEB_REQUEST.getTitle())
                    .content(MessageType.WEB_REQUEST.getContent())
                    .color(Color.RED)
                    .agentName(metadataWrapper.metadata().agentName())
                    .times(metadataWrapper.metadata().startTime())
                    .description(MessageType.WEB_REQUEST.getBody(), metadataWrapper.data()
                            .tomcatWebServerInfo()
                            .tomcatRequestInfo()
                            .requestCount()
                    ).build();
        }
    },
    WEB_THREAD("Web thread alert!!", "[[ Alert ]] !!", "Web thread usage has exceeded the threshold: {} !!") {
        @Override
        public Message doMessage(final String url, final MetadataWrapper<SystemInfo> metadataWrapper) {

            return MessageMaker.builder()
                    .url(url)
                    .title(MessageType.WEB_THREAD.getTitle())
                    .content(MessageType.WEB_THREAD.getContent())
                    .color(Color.RED)
                    .agentName(metadataWrapper.metadata().agentName())
                    .times(metadataWrapper.metadata().startTime())
                    .description(MessageType.WEB_THREAD.getBody(), metadataWrapper.data()
                            .tomcatWebServerInfo()
                            .tomcatThreadPoolInfo()
                            .currentThreads()
                    ).build();
        }
    },
    DBCP("Dbcp alert!!", "[[ Alert ]] !!", "Dbcp usage has exceeded the threshold: {} !!") {
        @Override
        public Message doMessage(final String url, final MetadataWrapper<SystemInfo> metadataWrapper) {

            return MessageMaker.builder()
                    .url(url)
                    .title(MessageType.DBCP.getTitle())
                    .content(MessageType.DBCP.getContent())
                    .color(Color.RED)
                    .agentName(metadataWrapper.metadata().agentName())
                    .times(metadataWrapper.metadata().startTime())
                    .description(MessageType.DBCP.getBody(), metadataWrapper.data().hikariDbcpInfo().activeConnections())
                    .build();
        }
    },
    ;

    private final String title;

    private final String content;

    private final String body;

    MessageType(final String title, final String content, final String body) {
        this.title = title;
        this.content = content;
        this.body = body;
    }

    public abstract Message doMessage(String url, MetadataWrapper<SystemInfo> metadataWrapper);

    enum Language {
        KOR,
        LNG
    }
}
