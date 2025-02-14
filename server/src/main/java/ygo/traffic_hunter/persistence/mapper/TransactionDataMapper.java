package ygo.traffic_hunter.persistence.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import org.springframework.jdbc.core.RowMapper;
import ygo.traffic_hunter.domain.metric.TransactionData;

public class TransactionDataMapper implements RowMapper<TransactionData> {

    @Override
    @SuppressWarnings("unchecked")
    public TransactionData mapRow(final ResultSet rs, final int rowNum) throws SQLException {

        return new TransactionData(
                rs.getString("name"),
                rs.getString("traceId"),
                rs.getString("parentSpanId"),
                rs.getString("spanId"),
                rs.getObject("attributes", Map.class),
                rs.getInt("attributesCount"),
                rs.getTimestamp("startTime").toInstant(),
                rs.getTimestamp("endTime").toInstant(),
                rs.getLong("duration"),
                rs.getString("exception"),
                rs.getBoolean("ended")
        );
    }
}
