/**
 * The MIT License
 *
 * Copyright (c) 2024 traffic-hunter.org
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
package ygo.traffic_hunter.persistence.mapper.statistics;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZoneOffset;
import org.springframework.jdbc.core.RowMapper;
import ygo.traffic_hunter.core.dto.response.statistics.transaction.ServiceTransactionResponse;

/**
 * @author yungwang-o
 * @version 1.1.0
 */
public class StatisticsServiceTransactionRowMapper implements RowMapper<ServiceTransactionResponse> {

    @Override
    public ServiceTransactionResponse mapRow(final ResultSet rs, final int rowNum) throws SQLException {
        return new ServiceTransactionResponse(
                rs.getTimestamp("timestamp").toInstant().atOffset(ZoneOffset.UTC),
                rs.getString("uri"),
                rs.getLong("duration"),
                rs.getString("httpMethod"),
                rs.getString("agentName"),
                rs.getString("clientName"),
                rs.getInt("httpStatusCode"),
                rs.getString("traceId")
        );
    }
}
