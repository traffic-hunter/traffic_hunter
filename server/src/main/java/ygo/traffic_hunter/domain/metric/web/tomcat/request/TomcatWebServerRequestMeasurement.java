package ygo.traffic_hunter.domain.metric.web.tomcat.request;

/**
 * @author yungwang-o
 * @version 1.0.0
 */
public record TomcatWebServerRequestMeasurement(
        long requestCount,
        long bytesReceived,
        long bytesSent,
        long processingTime,
        long errorCount
) {
}
