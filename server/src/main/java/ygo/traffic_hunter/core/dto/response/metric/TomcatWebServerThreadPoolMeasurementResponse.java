package ygo.traffic_hunter.core.dto.response.metric;

public record TomcatWebServerThreadPoolMeasurementResponse(
        int maxThreads,
        int currentThreads,
        int currentThreadsBusy
) {
}
