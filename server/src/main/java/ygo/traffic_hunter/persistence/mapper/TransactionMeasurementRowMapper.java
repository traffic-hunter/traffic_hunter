/**
 * The MIT License
 *
 * Copyright (c) 2024 yungwang-o
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package ygo.traffic_hunter.persistence.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ygo.traffic_hunter.core.dto.response.SystemMetricResponse;
import ygo.traffic_hunter.core.dto.response.TransactionMetricResponse;
import ygo.traffic_hunter.domain.entity.TransactionMeasurement;
import ygo.traffic_hunter.domain.metric.MetricData;
import ygo.traffic_hunter.domain.metric.TransactionData;

/**
 * @author yungwang-o
 * @version 1.0.0
 */
@Component
public class TransactionMeasurementRowMapper extends RowMapSupport<TransactionData> implements RowMapper<TransactionMetricResponse> {

    public TransactionMeasurementRowMapper(final ObjectMapper objectMapper) {
        super(objectMapper);
    }

    @Override
    public TransactionMetricResponse mapRow(final ResultSet rs, final int rowNum) throws SQLException {
        return TransactionMetricResponse.create(
                rs.getString("agent_name"),
                rs.getTimestamp("agent_boot_time").toInstant(),
                rs.getString("agent_version"),
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
