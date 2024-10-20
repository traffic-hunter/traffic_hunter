package ygo.traffic_hunter.config.tsdb;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.WriteApiBlocking;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ygo.traffic_hunter.common.property.InfluxDBProperty;

@Configuration
@RequiredArgsConstructor
public class InfluxDBConfig {

    @Bean
    public InfluxDBClient influxDB(final InfluxDBProperty property) {
        return InfluxDBClientFactory.create(property.url());
    }

    @Bean
    public WriteApiBlocking writeApi(final InfluxDBClient influxDBClient) {
        return influxDBClient.getWriteApiBlocking();
    }
}
