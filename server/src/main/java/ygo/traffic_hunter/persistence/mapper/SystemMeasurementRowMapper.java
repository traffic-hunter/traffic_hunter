/**
 * The MIT License
 * <p>
 * Copyright (c) 2024 yungwang-o
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the
 * Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package ygo.traffic_hunter.persistence.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ygo.traffic_hunter.core.dto.response.SystemMetricResponse;
import ygo.traffic_hunter.domain.metric.MetricData;
import ygo.traffic_hunter.domain.metric.cpu.CpuMetricMeasurement;
import ygo.traffic_hunter.domain.metric.dbcp.hikari.HikariCPMeasurement;
import ygo.traffic_hunter.domain.metric.memory.MemoryMetricMeasurement;
import ygo.traffic_hunter.domain.metric.memory.usage.MemoryMetricUsage;
import ygo.traffic_hunter.domain.metric.thread.ThreadMetricMeasurement;
import ygo.traffic_hunter.domain.metric.web.tomcat.TomcatWebServerMeasurement;
import ygo.traffic_hunter.domain.metric.web.tomcat.request.TomcatWebServerRequestMeasurement;
import ygo.traffic_hunter.domain.metric.web.tomcat.thread.TomcatWebServerThreadPoolMeasurement;

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
                getMetricData(rs)
        );
    }

    private MetricData getMetricData(final ResultSet rs) throws SQLException {
        // CPU Metric
        CpuMetricMeasurement cpuMetric = new CpuMetricMeasurement(
                rs.getDouble("system_cpu_load"),
                rs.getDouble("process_cpu_load"),
                rs.getInt("available_processors")
        );

        // Memory Metric
        MemoryMetricMeasurement memoryMetric = new MemoryMetricMeasurement(
                new MemoryMetricUsage(
                        rs.getLong("heap_init"),
                        rs.getLong("heap_used"),
                        rs.getLong("heap_committed"),
                        rs.getLong("heap_max")
                )
        );

        // Thread Metric
        ThreadMetricMeasurement threadMetric = new ThreadMetricMeasurement(
                rs.getInt("thread_count"),
                rs.getInt("peak_thread_count"),
                rs.getInt("total_started_thread_count")
        );

        // Web Server Metric
        TomcatWebServerMeasurement webServerMetric = new TomcatWebServerMeasurement(
                new TomcatWebServerRequestMeasurement(
                        rs.getLong("request_count"),
                        rs.getLong("bytes_received"),
                        rs.getLong("bytes_sent"),
                        rs.getLong("processing_time"),
                        rs.getLong("error_count")
                ),
                new TomcatWebServerThreadPoolMeasurement(
                        rs.getInt("max_threads"),
                        rs.getInt("current_threads"),
                        rs.getInt("current_threads_busy")
                )
        );

        // DBCP Metric
        HikariCPMeasurement dbcpMetric = new HikariCPMeasurement(
                rs.getInt("active_connections"),
                rs.getInt("idle_connections"),
                rs.getInt("total_connections"),
                rs.getInt("threads_awaiting_connection")
        );

        return new MetricData(
                cpuMetric,
                memoryMetric,
                threadMetric,
                webServerMetric,
                dbcpMetric
        );
    }

    public String serialize(final MetricData metricData) {
        return super.serialize0(metricData);
    }
}
