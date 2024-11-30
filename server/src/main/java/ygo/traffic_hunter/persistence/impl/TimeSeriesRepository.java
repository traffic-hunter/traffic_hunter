package ygo.traffic_hunter.persistence.impl;

import java.sql.Timestamp;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ygo.traffic_hunter.core.repository.MetricRepository;
import ygo.traffic_hunter.domain.entity.MetricMeasurement;
import ygo.traffic_hunter.domain.entity.TransactionMeasurement;
import ygo.traffic_hunter.domain.interval.TimeInterval;
import ygo.traffic_hunter.persistence.mapper.SystemMeasurementRowMapper;
import ygo.traffic_hunter.persistence.mapper.TransactionMeasurementRowMapper;

@Slf4j
@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TimeSeriesRepository implements MetricRepository {

    private final JdbcTemplate jdbcTemplate;

    private final SystemMeasurementRowMapper systemMeasurementRowMapper;

    private final TransactionMeasurementRowMapper txMeasurementRowMapper;

    @Override
    @Transactional
    public void save(final MetricMeasurement metric) {

        String sql = "insert into metric_measurement ("
                + "time, "
                + "agent_id, "
                + "agent_name, "
                + "agent_version, "
                + "agent_boot_time, "
                + "metric_data) "
                + "values (?, ?, ?, ?, ?, ?::jsonb)";

        jdbcTemplate.update(sql,
                Timestamp.from(metric.time()),
                metric.agentId(),
                metric.agentName(),
                metric.agentVersion(),
                Timestamp.from(metric.agentBootTime()),
                systemMeasurementRowMapper.serialize(metric.metricData())
                );
    }

    @Override
    @Transactional
    public void save(final TransactionMeasurement metric) {
        String sql = "insert into transaction_measurement ("
                + "time, "
                + "agent_id, "
                + "agent_name, "
                + "agent_version, "
                + "agent_boot_time, "
                + "transaction_data) "
                + "values (?, ?, ?, ?, ?, ?::jsonb)";

        jdbcTemplate.update(sql,
                Timestamp.from(metric.time()),
                metric.agentId(),
                metric.agentName(),
                metric.agentVersion(),
                Timestamp.from(metric.agentBootTime()),
                txMeasurementRowMapper.serialize(metric.transactionData())
        );
    }

    @Override
    public List<MetricMeasurement> findMetricsByRecentTimeAndAgentName(final TimeInterval interval,
                                                                       final String agentName) {

        String sql = "select * from metric_measurement "
                + "where time > now() - ?::interval "
                + "and agent_name = ? "
                + "order by time desc";

        return jdbcTemplate.query(sql, systemMeasurementRowMapper, interval.getInterval(), agentName);
    }

    @Override
    public List<TransactionMeasurement> findTxMetricsByRecentTimeAndAgentName(final TimeInterval interval,
                                                                              final String agentName) {

        String sql = "select * from transaction_measurement "
                + "where time > now() - ?::interval "
                + "and agent_name = ? "
                + "order by time desc "
                + "limit 20";

        return jdbcTemplate.query(sql, txMeasurementRowMapper, interval.getInterval(), agentName);
    }

    @Transactional
    public void clear() {
        jdbcTemplate.update("truncate table metric_measurement");
    }
}
