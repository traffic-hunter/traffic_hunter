package ygo.traffic_hunter.persistence.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ygo.traffic_hunter.domain.entity.TransactionMeasurement;
import ygo.traffic_hunter.domain.metric.TransactionData;

@Component
public class TransactionMeasurementRowMapper extends RowMapSupport<TransactionData> implements RowMapper<TransactionMeasurement> {

    public TransactionMeasurementRowMapper(final ObjectMapper objectMapper) {
        super(objectMapper);
    }

    @Override
    public TransactionMeasurement mapRow(final ResultSet rs, final int rowNum) throws SQLException {
        return new TransactionMeasurement(
                rs.getTimestamp("time").toInstant(),
                rs.getString("agent_id"),
                rs.getString("agent_name"),
                rs.getString("agent_version"),
                rs.getTimestamp("agent_boot_time").toInstant(),
                deserialize(rs.getString("metrics"), TransactionData.class)
        );
    }

    public String serialize(final TransactionData txData) {
        return super.serialize(txData);
    }
}
