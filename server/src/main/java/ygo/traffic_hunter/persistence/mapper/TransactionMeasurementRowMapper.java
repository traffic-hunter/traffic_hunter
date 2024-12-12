package ygo.traffic_hunter.persistence.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ygo.traffic_hunter.domain.entity.TransactionMeasurement;
import ygo.traffic_hunter.domain.metric.TransactionData;

/**
 * @author yungwang-o
 * @version 1.0.0
 */
@Component
public class TransactionMeasurementRowMapper extends RowMapSupport<TransactionData> implements RowMapper<TransactionMeasurement> {

    public TransactionMeasurementRowMapper(final ObjectMapper objectMapper) {
        super(objectMapper);
    }

    @Override
    public TransactionMeasurement mapRow(final ResultSet rs, final int rowNum) throws SQLException {
        return new TransactionMeasurement(
                rs.getTimestamp("time").toInstant(),
                rs.getInt("agent_id"),
                deserialize(rs.getString("transaction_data"), TransactionData.class)
        );
    }

    public String serialize(final List<TransactionData> txData) {
        return super.serialize0(txData);
    }

    public String serialize(final TransactionData txData) {
        return super.serialize0(txData);
    }
}
