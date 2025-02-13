package ygo.traffic_hunter.core.dto.response.metric;

public record TomcatWebServerMeasurementResponse(
        TomcatWebServerRequestMeasurementResponse tomcatWebServerRequestMeasurement,
        TomcatWebServerThreadPoolMeasurementResponse tomcatWebServerThreadPoolMeasurement
) {
}
