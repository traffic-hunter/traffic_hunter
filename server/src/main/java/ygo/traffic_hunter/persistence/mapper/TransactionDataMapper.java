package ygo.traffic_hunter.persistence.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ygo.traffic_hunter.domain.metric.TransactionData;

@Component
public class TransactionDataMapper extends RowMapSupport<String> implements RowMapper<TransactionData> {

    public TransactionDataMapper(final ObjectMapper objectMapper) {
        super(objectMapper);
    }

    @Override
    @SuppressWarnings("unchecked")
    public TransactionData mapRow(final ResultSet rs, final int rowNum) throws SQLException {

        return new TransactionData(
                rs.getString("name"),
                rs.getString("traceId"),
                rs.getString("parentSpanId"),
                rs.getString("spanId"),
                deserialize(rs.getString("attributes"), Map.class),
                rs.getInt("attributesCount"),
                rs.getTimestamp("startTime").toInstant(),
                rs.getTimestamp("endTime").toInstant(),
                rs.getLong("duration"),
                rs.getString("exception"),
                rs.getBoolean("ended")
        );
    }
}
