package ygo.traffic_hunter.core.webhook.message;

import java.net.InetAddress;
import lombok.Getter;
import ygo.traffic_hunter.core.dto.request.metadata.MetadataWrapper;
import ygo.traffic_hunter.core.dto.request.systeminfo.SystemInfo;
import ygo.traffic_hunter.core.webhook.message.library.MessageMaker;
import ygo.traffic_hunter.core.webhook.message.library.MessageMaker.Color;

@Getter
public enum WebHookMessageType {

    CPU("Cpu alert!!", "[[ Alert ]] !!", "CPU usage has exceeded the threshold: {}% !!") {
        @Override
        public Message doMessage(final String url, final InetAddress inetAddress, final MetadataWrapper<SystemInfo> metadataWrapper) {

            return MessageMaker.builder()
                    .url(url)
                    .title(WebHookMessageType.CPU.getTitle())
                    .content(WebHookMessageType.CPU.getContent())
                    .color(Color.RED)
                    .inet(inetAddress)
                    .agentName(metadataWrapper.metadata().agentName())
                    .times(metadataWrapper.metadata().startTime())
                    .description(WebHookMessageType.CPU.getBody(), metadataWrapper.data().cpuStatusInfo().processCpuLoad())
                    .build();
        }
    },
    MEMORY("Memory alert!!", "[[ Alert ]] !!", "Memory usage has exceeded the threshold: {} !!") {
        @Override
        public Message doMessage(final String url, final InetAddress inetAddress, final MetadataWrapper<SystemInfo> metadataWrapper) {

            String used = String.format("%.1f", (double) metadataWrapper.data()
                    .memoryStatusInfo()
                    .heapMemoryUsage()
                    .used() / (1024 * 1024));

            return MessageMaker.builder()
                    .url(url)
                    .title(WebHookMessageType.MEMORY.getTitle())
                    .content(WebHookMessageType.MEMORY.getContent())
                    .color(Color.RED)
                    .inet(inetAddress)
                    .agentName(metadataWrapper.metadata().agentName())
                    .times(metadataWrapper.metadata().startTime())
                    .description(WebHookMessageType.MEMORY.getBody(), used)
                    .build();
        }
    },
    THREAD("Thread alert!!", "[[ Alert ]] !!", "Thread usage has exceeded the threshold: {} !!") {
        @Override
        public Message doMessage(final String url, final InetAddress inetAddress, final MetadataWrapper<SystemInfo> metadataWrapper) {

            return MessageMaker.builder()
                    .url(url)
                    .title(WebHookMessageType.THREAD.getTitle())
                    .content(WebHookMessageType.THREAD.getContent())
                    .color(Color.RED)
                    .inet(inetAddress)
                    .agentName(metadataWrapper.metadata().agentName())
                    .times(metadataWrapper.metadata().startTime())
                    .description(WebHookMessageType.THREAD.getBody(), metadataWrapper.data().threadStatusInfo().threadCount())
                    .build();
        }
    },
    WEB_REQUEST("Web request alert!!", "[[ Alert ]] !!", "Web request usage has exceeded the threshold: {} !!") {
        @Override
        public Message doMessage(final String url, final InetAddress inetAddress, final MetadataWrapper<SystemInfo> metadataWrapper) {

            return MessageMaker.builder()
                    .url(url)
                    .title(WebHookMessageType.WEB_REQUEST.getTitle())
                    .content(WebHookMessageType.WEB_REQUEST.getContent())
                    .color(Color.RED)
                    .inet(inetAddress)
                    .agentName(metadataWrapper.metadata().agentName())
                    .times(metadataWrapper.metadata().startTime())
                    .description(WebHookMessageType.WEB_REQUEST.getBody(), metadataWrapper.data()
                            .tomcatWebServerInfo()
                            .tomcatRequestInfo()
                            .requestCount()
                    ).build();
        }
    },
    WEB_THREAD("Web thread alert!!", "[[ Alert ]] !!", "Web thread usage has exceeded the threshold: {} !!") {
        @Override
        public Message doMessage(final String url, final InetAddress inetAddress, final MetadataWrapper<SystemInfo> metadataWrapper) {

            return MessageMaker.builder()
                    .url(url)
                    .title(WebHookMessageType.WEB_THREAD.getTitle())
                    .content(WebHookMessageType.WEB_THREAD.getContent())
                    .color(Color.RED)
                    .inet(inetAddress)
                    .agentName(metadataWrapper.metadata().agentName())
                    .times(metadataWrapper.metadata().startTime())
                    .description(WebHookMessageType.WEB_THREAD.getBody(), metadataWrapper.data()
                            .tomcatWebServerInfo()
                            .tomcatThreadPoolInfo()
                            .currentThreads()
                    ).build();
        }
    },
    DBCP("Dbcp alert!!", "[[ Alert ]] !!", "Dbcp usage has exceeded the threshold: {} !!") {
        @Override
        public Message doMessage(final String url, final InetAddress inetAddress, final MetadataWrapper<SystemInfo> metadataWrapper) {

            return MessageMaker.builder()
                    .url(url)
                    .title(WebHookMessageType.DBCP.getTitle())
                    .content(WebHookMessageType.DBCP.getContent())
                    .color(Color.RED)
                    .inet(inetAddress)
                    .agentName(metadataWrapper.metadata().agentName())
                    .times(metadataWrapper.metadata().startTime())
                    .description(WebHookMessageType.DBCP.getBody(), metadataWrapper.data().hikariDbcpInfo().activeConnections())
                    .build();
        }
    },
    ;

    private final String title;

    private final String content;

    private final String body;

    WebHookMessageType(final String title, final String content, final String body) {
        this.title = title;
        this.content = content;
        this.body = body;
    }

    public abstract Message doMessage(String url, InetAddress inetAddress, MetadataWrapper<SystemInfo> metadataWrapper);

    enum Language {
        KOR,
        LNG
    }
}
