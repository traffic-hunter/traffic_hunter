package ygo.traffic_hunter.persistence.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ygo.traffic_hunter.core.dto.response.SystemMetricResponse;
import ygo.traffic_hunter.domain.metric.MetricData;

/**
 * @author yungwang-o
 * @version 1.0.0
 */
@Component
public class SystemMeasurementRowMapper extends RowMapSupport<MetricData> implements RowMapper<SystemMetricResponse> {

    public SystemMeasurementRowMapper(final ObjectMapper objectMapper) {
        super(objectMapper);
    }

    @Override
    public SystemMetricResponse mapRow(final ResultSet rs, final int rowNum) throws SQLException {
        return SystemMetricResponse.create(
                rs.getTimestamp("time").toInstant(),
                rs.getString("agent_name"),
                rs.getTimestamp("agent_boot_time").toInstant(),
                rs.getString("agent_version"),
                deserialize(rs.getString("metric_data"), MetricData.class)
        );
    }

    public String serialize(final MetricData metricData) {
        return super.serialize0(metricData);
    }
}
