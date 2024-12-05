package ygo.traffic_hunter.persistence.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ygo.traffic_hunter.domain.entity.MetricMeasurement;
import ygo.traffic_hunter.domain.metric.MetricData;

@Component
public class SystemMeasurementRowMapper extends RowMapSupport<MetricData> implements RowMapper<MetricMeasurement> {

    public SystemMeasurementRowMapper(final ObjectMapper objectMapper) {
        super(objectMapper);
    }

    @Override
    public MetricMeasurement mapRow(final ResultSet rs, final int rowNum) throws SQLException {
        return new MetricMeasurement(
                rs.getTimestamp("time").toInstant(),
                rs.getInt("agent_id"),
                deserialize(rs.getString("metric_data"), MetricData.class)
        );
    }

    public String serialize(final MetricData metricData) {
        return super.serialize(metricData);
    }
}
