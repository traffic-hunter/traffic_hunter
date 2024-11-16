package ygo.traffic_hunter.common.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(
        prefix = "spring.influx"
)
public record InfluxDBProperty(
        String url,
        String organizationName,
        String bucket,
        String token
) {
}
