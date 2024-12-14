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
package ygo.traffic_hunter.persistence.impl;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ygo.traffic_hunter.config.cache.CacheConfig.CacheType;
import ygo.traffic_hunter.core.dto.response.SystemMetricResponse;
import ygo.traffic_hunter.core.dto.response.TransactionMetricResponse;
import ygo.traffic_hunter.core.repository.MetricRepository;
import ygo.traffic_hunter.domain.entity.Agent;
import ygo.traffic_hunter.domain.entity.MetricMeasurement;
import ygo.traffic_hunter.domain.entity.TransactionMeasurement;
import ygo.traffic_hunter.domain.interval.TimeInterval;
import ygo.traffic_hunter.persistence.mapper.AgentRowMapper;
import ygo.traffic_hunter.persistence.mapper.SystemMeasurementRowMapper;
import ygo.traffic_hunter.persistence.mapper.TransactionMeasurementRowMapper;

/**
 * @author yungwang-o
 * @version 1.0.0
 */
@Slf4j
@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TimeSeriesRepository implements MetricRepository {

    private final JdbcTemplate jdbcTemplate;

    private final SystemMeasurementRowMapper systemMeasurementRowMapper;

    private final TransactionMeasurementRowMapper txMeasurementRowMapper;

    private final AgentRowMapper agentRowMapper;

    @Override
    @Transactional
    public void save(final Agent agent) {

        String sql = "insert into agent ("
                + "agent_id, "
                + "agent_name, "
                + "agent_version, "
                + "agent_boot_time) "
                + "VALUES (?, ?, ?, ?)";

        jdbcTemplate.update(sql,
                agent.agentId(),
                agent.agentName(),
                agent.agentVersion(),
                Timestamp.from(agent.agentBootTime())
        );
    }

    @Override
    @Transactional
    public void save(final MetricMeasurement metric) {

        String sql = "insert into metric_measurement ("
                + "time, "
                + "agent_id, "
                + "metric_data) "
                + "values (?, ?, ?::jsonb)";

        jdbcTemplate.update(sql,
                Timestamp.from(metric.time()),
                metric.agentId(),
                systemMeasurementRowMapper.serialize(metric.metricData())
        );
    }

    @Override
    @Transactional
    public void save(final TransactionMeasurement metric) {

        String sql = "insert into transaction_measurement ("
                + "time, "
                + "agent_id, "
                + "transaction_data) "
                + "values (?, ?, ?::jsonb)";

        jdbcTemplate.update(sql,
                Timestamp.from(metric.time()),
                metric.agentId(),
                txMeasurementRowMapper.serialize(metric.transactionData())
        );
    }

    @Override
    @Cacheable(cacheNames = CacheType.AGENT_CACHE_NAME)
    public Agent findById(final Integer id) {

        String sql = "select * from agent where id = ?::integer";

        return Optional.ofNullable(jdbcTemplate.queryForObject(sql, agentRowMapper, id))
                .orElseThrow(() -> new IllegalArgumentException("Agent not found"));
    }

    @Override
    @Cacheable(cacheNames = CacheType.AGENT_CACHE_NAME)
    public List<Agent> findAll() {

        String sql = "select * from agent";

        return jdbcTemplate.query(sql, agentRowMapper);
    }

    @Override
    @Cacheable(cacheNames = CacheType.AGENT_CACHE_NAME)
    public Agent findByAgentName(final String agentName) {

        String sql = "select * from agent where agent_name = ?";

        return Optional.ofNullable(jdbcTemplate.queryForObject(sql, agentRowMapper, agentName))
                .orElseThrow(() -> new IllegalArgumentException("Agent not found"));
    }

    @Override
    @Cacheable(cacheNames = CacheType.AGENT_CACHE_NAME)
    public Agent findByAgentId(final String agentId) {

        String sql = "select * from agent where agent_id = ?";

        return Optional.ofNullable(jdbcTemplate.queryForObject(sql, agentRowMapper, agentId))
                .orElseThrow(() -> new IllegalArgumentException("Agent not found"));
    }

    @Override
    public boolean existsByAgentId(final String agentId) {

        String sql = "select exists (select 1 from agent where agent_id = ?)";

        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Boolean.class, agentId));
    }

    @Override
    public boolean existsByAgentName(final String agentName) {

        String sql = "select exists (select 1 from agent where agent_name = ?)";

        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Boolean.class, agentName));
    }

    @Override
    public List<SystemMetricResponse> findMetricsByRecentTimeAndAgentName(final TimeInterval interval,
                                                                          final String agentName) {

        String sql = "select m.time, a.agent_name, a.agent_boot_time, a.agent_version, m.metric_data "
                + "from metric_measurement m "
                + "join agent a on m.agent_id = a.id "
                + "where m.time > now() - ?::interval "
                + "and a.agent_name = ? "
                + "order by m.time desc "
                + "limit 20";

        return jdbcTemplate.query(sql, systemMeasurementRowMapper, interval.getInterval(), agentName);
    }

    @Override
    public List<TransactionMetricResponse> findTxMetricsByRecentTimeAndAgentName(final TimeInterval interval,
                                                                                 final String agentName) {

        return null;
    }

    @Transactional
    public void clear() {
        jdbcTemplate.update("truncate table metric_measurement");
    }
}
