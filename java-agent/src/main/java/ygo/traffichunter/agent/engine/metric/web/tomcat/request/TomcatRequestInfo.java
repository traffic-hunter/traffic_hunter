package ygo.traffichunter.agent.engine.metric.web.tomcat.request;

/**
 * @author yungwang-o
 * @version 1.0.0
 */
public record TomcatRequestInfo(
        long requestCount,
        long bytesReceived,
        long bytesSent,
        long processingTime,
        long errorCount
) {
}
