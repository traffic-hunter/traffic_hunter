package ygo.traffic_hunter.core.dto.request.systeminfo.web.tomcat.request;

public record TomcatRequestInfo(
        long requestCount,
        long bytesReceived,
        long bytesSent,
        long processingTime,
        long errorCount
) {
}
