package ygo.traffic_hunter.domain.metric.web.tomcat.request;

public record TomcatWebServerRequestMeasurement(
        long requestCount,
        long bytesReceived,
        long bytesSent,
        long processingTime,
        long errorCount
) {
}
