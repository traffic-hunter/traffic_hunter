package ygo.traffichunter.agent.engine.metric.web.tomcat.request;

public record TomcatRequestInfo(
        long requestCount,
        long bytesReceived,
        long bytesSent,
        long processingTime,
        long errorCount
) {
}
