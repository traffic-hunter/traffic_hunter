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
package ygo.traffic_hunter.persistence.impl;

import static org.jooq.impl.DSL.asterisk;
import static org.jooq.impl.DSL.avg;
import static org.jooq.impl.DSL.count;
import static org.jooq.impl.DSL.inline;
import static org.jooq.impl.DSL.jsonbGetAttribute;
import static org.jooq.impl.DSL.jsonbGetAttributeAsText;
import static org.jooq.impl.DSL.max;
import static org.jooq.impl.DSL.sum;
import static org.jooq.impl.DSL.when;
import static org.traffichunter.query.jooq.Tables.TRANSACTION_MEASUREMENT;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.JSONB;
import org.jooq.Record6;
import org.jooq.SelectLimitPercentStep;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ygo.traffic_hunter.config.cache.CacheConfig.CacheType;
import ygo.traffic_hunter.core.dto.response.RealTimeMonitoringResponse;
import ygo.traffic_hunter.core.dto.response.SystemMetricResponse;
import ygo.traffic_hunter.core.dto.response.TransactionMetricResponse;
import ygo.traffic_hunter.core.dto.response.statistics.metric.StatisticsMetricAvgResponse;
import ygo.traffic_hunter.core.dto.response.statistics.metric.StatisticsMetricMaxResponse;
import ygo.traffic_hunter.core.dto.response.statistics.transaction.ServiceTransactionResponse;
import ygo.traffic_hunter.core.repository.MetricRepository;
import ygo.traffic_hunter.core.statistics.StatisticsMetricTimeRange;
import ygo.traffic_hunter.domain.entity.Agent;
import ygo.traffic_hunter.domain.entity.MetricMeasurement;
import ygo.traffic_hunter.domain.entity.TransactionMeasurement;
import ygo.traffic_hunter.domain.interval.TimeInterval;
import ygo.traffic_hunter.persistence.mapper.AgentRowMapper;
import ygo.traffic_hunter.persistence.mapper.SystemMeasurementRowMapper;
import ygo.traffic_hunter.persistence.mapper.TransactionMeasurementRowMapper;
import ygo.traffic_hunter.persistence.mapper.statistics.StatisticsMetricAvgRowMapper;
import ygo.traffic_hunter.persistence.mapper.statistics.StatisticsMetricMaxRowMapper;
import ygo.traffic_hunter.persistence.mapper.statistics.StatisticsServiceTransactionRowMapper;
import ygo.traffic_hunter.persistence.query.QuerySupport;

/**
 * @author yungwang-o, JuSeong
 * @version 1.1.0
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

    private final DSLContext dsl;

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
                                                                          final String agentName,
                                                                          final Integer limit) {

        String sql = "select m.time, a.agent_name, a.agent_boot_time, a.agent_version, m.metric_data "
                + "from metric_measurement m "
                + "join agent a on m.agent_id = a.id "
                + "where m.time > now() - ?::interval "
                + "and a.agent_name = ? "
                + "order by m.time desc "
                + "limit ?";

        return jdbcTemplate.query(sql, systemMeasurementRowMapper, interval.getInterval(), agentName, limit);
    }

    @Override
    public List<TransactionMetricResponse> findTxMetricsByRecentTimeAndAgentName(final TimeInterval interval,
                                                                                 final String agentName,
                                                                                 final Integer limit) {
        String sql =
                "SELECT a.agent_name, a.agent_boot_time, a.agent_version, JSON_AGG(t.transaction_data) AS transaction_datas "
                        + "FROM transaction_measurement t "
                        + "JOIN agent a ON t.agent_id = a.id "
                        + "WHERE t.time > now() - ?::interval "
                        + "AND a.agent_name = ? "
                        + "GROUP BY a.agent_name, a.agent_boot_time, a.agent_version, t.transaction_data->>'traceId' "
                        + "LIMIT ?";

        return jdbcTemplate.query(sql, txMeasurementRowMapper, interval.getInterval(), agentName, limit);
    }

    @Override
    public List<RealTimeMonitoringResponse> findRealtimeMonitoringByTimeInterval(final TimeInterval timeInterval,
                                                                                 final String agentName,
                                                                                 final Integer limit) {

        return List.of();
    }

    @Transactional
    public void clear() {
        jdbcTemplate.update("truncate table metric_measurement");
    }

    @Override
    @Cacheable(
            cacheNames = CacheType.STATISTIC_TRANSACTION_PAGE_CACHE_NAME,
            key = "#{pageable.pageNumber}",
            condition = "#{pageable.pageNumber == 0}"
    )
    public Slice<ServiceTransactionResponse> findServiceTransactionByBeginToEnd(final Pageable pageable) {

        org.traffichunter.query.jooq.tables.TransactionMeasurement tm = TRANSACTION_MEASUREMENT;

        Field<JSONB> attributes = jsonbGetAttribute(tm.TRANSACTION_DATA, inline("attributes"));

        Field<String> urlField = jsonbGetAttributeAsText(attributes, inline("http.requestURI"));

        Field<Boolean> ended = jsonbGetAttributeAsText(tm.TRANSACTION_DATA, inline("ended")).cast(Boolean.class);

        Field<Long> duration = jsonbGetAttributeAsText(tm.TRANSACTION_DATA, inline("duration")).cast(Long.class);

        SelectLimitPercentStep<Record6<String, Integer, BigDecimal, BigDecimal, BigDecimal, Long>> result =
                dsl.select(
                        urlField.as("url"),
                        count(asterisk()).as("count"),
                        sum(when(ended.eq(inline(false)), inline(1)).otherwise(inline(0))).as("err_count"),
                        avg(duration).as("avg_execution_time"),
                        sum(duration).as("sum_execution_time"),
                        max(duration).as("max_execution_time")
                )
                .from(tm)
                .where(urlField.isNotNull())
                .groupBy(urlField, tm.TIME)
                .orderBy(QuerySupport.orderByClause(pageable))
                .limit(pageable.getPageSize() + 1);

        int pageSize = pageable.getPageSize();

        List<ServiceTransactionResponse> results = jdbcTemplate.query(
                result.getSQL(),
                new StatisticsServiceTransactionRowMapper(),
                result.getBindValues().toArray()
        );

        boolean hasNext = results.size() > pageSize;
        if(hasNext) {
            results = results.subList(0, pageSize);
        }

        return new SliceImpl<>(results, pageable, hasNext);
    }

    @Override
    public StatisticsMetricMaxResponse findMaxMetricByTimeInterval(final StatisticsMetricTimeRange timeRange) {

        String sql = "select "
                + "time_bucket(?::interval, time) as period, "
                + "max(round((metric_data->'cpuMetric'->>'systemCpuLoad')::numeric, 1)) as max_system_cpu_usage, "
                + "max(round((metric_data->'cpuMetric'->>'systemCpuLoad')::numeric, 1)) as max_process_cpu_usage, "
                + "max(round((metric_data->'memoryMetric'->'heapMemoryUsage'->>'used')::numeric / 1000000, 1)) as max_heap_memory_usage, "
                + "max((metric_data->'threadMetric'->>'threadCount')::integer) as max_thread_count, "
                + "max((metric_data->'threadMetric'->>'getPeekThreadCount')::integer) as max_peak_thread_count, "
                + "max((metric_data->'webServerMetric'->'tomcatWebServerRequestMeasurement'->>'requestCount')::integer) as max_web_request_count, "
                + "max((metric_data->'webServerMetric'->'tomcatWebServerRequestMeasurement'->>'errorCount')::integer) as max_web_error_count, "
                + "max((metric_data->'webServerMetric'->'tomcatWebServerThreadPoolMeasurement'->>'currentThreads')::integer) as max_web_thread_count, "
                + "max((metric_data->'dbcpMetric'->>'activeConnections')::integer) as max_db_connection_count "
                + "from metric_measurement "
                + "group by period "
                + "order by period desc "
                + "limit 1";

        return Optional.ofNullable(jdbcTemplate.queryForObject(sql, new StatisticsMetricMaxRowMapper(), timeRange.getLatestRange()))
                .orElseThrow(() -> new IllegalArgumentException("not found max metric"));
    }

    @Override
    public StatisticsMetricAvgResponse findAvgMetricByTimeInterval(final StatisticsMetricTimeRange timeRange) {

        String sql = "select "
                + "time_bucket(?::interval, time) as period, "
                + "round(avg((metric_data->'cpuMetric'->>'systemCpuLoad')::numeric), 1) as avg_system_cpu_usage, "
                + "round(avg((metric_data->'cpuMetric'->>'systemCpuLoad')::numeric), 1) as avg_process_cpu_usage, "
                + "round(avg((metric_data->'memoryMetric'->'heapMemoryUsage'->>'used')::bigint / 1000000), 1) as avg_heap_memory_usage, "
                + "round(avg((metric_data->'threadMetric'->>'threadCount')::integer), 1) as avg_thread_count, "
                + "round(avg((metric_data->'threadMetric'->>'getPeekThreadCount')::integer), 1) as avg_peak_thread_count, "
                + "round(avg((metric_data->'webServerMetric'->'tomcatWebServerRequestMeasurement'->>'requestCount')::integer), 1) as avg_web_request_count, "
                + "round(avg((metric_data->'webServerMetric'->'tomcatWebServerRequestMeasurement'->>'errorCount')::integer), 1) as avg_web_error_count, "
                + "round(avg((metric_data->'webServerMetric'->'tomcatWebServerThreadPoolMeasurement'->>'currentThreads')::integer), 1) as avg_web_thread_count, "
                + "round(avg((metric_data->'dbcpMetric'->>'activeConnections')::integer), 1) as avg_db_connection_count "
                + "from metric_measurement "
                + "group by period "
                + "order by period desc "
                + "limit 1";

        return Optional.ofNullable(jdbcTemplate.queryForObject(sql, new StatisticsMetricAvgRowMapper(), timeRange.getLatestRange()))
                .orElseThrow(() -> new IllegalArgumentException("not found avg metric"));
    }
}
