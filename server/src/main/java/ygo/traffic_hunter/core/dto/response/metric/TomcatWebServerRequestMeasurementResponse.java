package ygo.traffic_hunter.core.dto.response.metric;

public record TomcatWebServerRequestMeasurementResponse(
        long requestCount,
        long bytesReceived,
        long bytesSent,
        long processingTime,
        long errorCount
) {
}
