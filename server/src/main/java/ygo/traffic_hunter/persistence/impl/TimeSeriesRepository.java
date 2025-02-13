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
import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.inline;
import static org.jooq.impl.DSL.jsonArrayAgg;
import static org.jooq.impl.DSL.jsonbGetAttribute;
import static org.jooq.impl.DSL.jsonbGetAttributeAsText;
import static org.jooq.impl.DSL.max;
import static org.jooq.impl.DSL.round;
import static org.jooq.impl.DSL.sum;
import static org.jooq.impl.DSL.when;
import static org.traffichunter.query.jooq.Tables.AGENT;
import static org.traffichunter.query.jooq.Tables.METRIC_MEASUREMENT;
import static org.traffichunter.query.jooq.Tables.TRANSACTION_MEASUREMENT;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.JSON;
import org.jooq.JSONB;
import org.jooq.Record;
import org.jooq.Record5;
import org.jooq.Record6;
import org.jooq.SelectLimitPercentStep;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ygo.traffic_hunter.config.cache.CacheConfig.CacheType;
import ygo.traffic_hunter.core.dto.response.RealTimeMonitoringResponse;
import ygo.traffic_hunter.core.dto.response.TransactionMetricResponse;
import ygo.traffic_hunter.core.dto.response.metric.SystemMetricResponse;
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
        org.traffichunter.query.jooq.tables.Agent agent = AGENT;
        org.traffichunter.query.jooq.tables.MetricMeasurement metricMeasurement = METRIC_MEASUREMENT;

        Field<Object> timeBucket = field("time_bucket({0}, {1})",
                inline(interval.getInterval()),
                metricMeasurement.TIME);

        Field<JSONB> metricData = metricMeasurement.METRIC_DATA;
        // CPU Metrics
        Field<BigDecimal> systemCpuLoad = getMetricField(metricData, "cpuMetric", "systemCpuLoad");
        Field<BigDecimal> processCpuLoad = getMetricField(metricData, "cpuMetric", "processCpuLoad");
        Field<BigDecimal> availableProcessors = getMetricField(metricData, "cpuMetric", "availableProcessors");

        // Memory Metrics - Heap
        Field<BigDecimal> heapInit = getMetricField(metricData, "memoryMetric", "heapMemoryUsage", "init");
        Field<BigDecimal> heapUsed = getMetricField(metricData, "memoryMetric", "heapMemoryUsage", "used");
        Field<BigDecimal> heapCommitted = getMetricField(metricData, "memoryMetric", "heapMemoryUsage",
                "committed");
        Field<BigDecimal> heapMax = getMetricField(metricData, "memoryMetric", "heapMemoryUsage", "max");

        // Thread Metrics
        Field<BigDecimal> threadCount = getMetricField(metricData, "threadMetric", "threadCount");
        Field<BigDecimal> peakThreadCount = getMetricField(metricData, "threadMetric", "getPeekThreadCount");
        Field<BigDecimal> totalStartedThreadCount = getMetricField(metricData, "threadMetric",
                "getTotalStartThreadCount");

        // Web Server Request Metrics
        Field<BigDecimal> requestCount = getMetricField(metricData, "webServerMetric",
                "tomcatWebServerRequestMeasurement", "requestCount");
        Field<BigDecimal> bytesReceived = getMetricField(metricData, "webServerMetric",
                "tomcatWebServerRequestMeasurement", "bytesReceived");
        Field<BigDecimal> bytesSent = getMetricField(metricData, "webServerMetric",
                "tomcatWebServerRequestMeasurement", "bytesSent");
        Field<BigDecimal> processingTime = getMetricField(metricData, "webServerMetric",
                "tomcatWebServerRequestMeasurement", "processingTime");
        Field<BigDecimal> errorCount = getMetricField(metricData, "webServerMetric",
                "tomcatWebServerRequestMeasurement", "errorCount");

        // Thread Pool Metrics
        Field<BigDecimal> maxThreads = getMetricField(metricData, "webServerMetric",
                "tomcatWebServerThreadPoolMeasurement", "maxThreads");
        Field<BigDecimal> currentThreads = getMetricField(metricData, "webServerMetric",
                "tomcatWebServerThreadPoolMeasurement", "currentThreads");
        Field<BigDecimal> currentThreadsBusy = getMetricField(metricData, "webServerMetric",
                "tomcatWebServerThreadPoolMeasurement", "currentThreadsBusy");

        // DBCP Metrics
        Field<BigDecimal> activeConnections = getMetricField(metricData, "dbcpMetric", "activeConnections");
        Field<BigDecimal> idleConnections = getMetricField(metricData, "dbcpMetric", "idleConnections");
        Field<BigDecimal> totalConnections = getMetricField(metricData, "cpuMetric", "totalConnections");
        Field<BigDecimal> threadsAwaitingConnection = getMetricField(metricData, "cpuMetric",
                "threadsAwaitingConnection");

        SelectLimitPercentStep<Record> result = dsl.select(
                        agent.AGENT_NAME,
                        agent.AGENT_BOOT_TIME,
                        agent.AGENT_VERSION,
                        timeBucket.as("time"),
                        // CPU Metrics
                        systemCpuLoad.as("system_cpu_load"),
                        processCpuLoad.as("process_cpu_load"),
                        availableProcessors.as("available_processors"),
                        // Memory Metrics - Heap
                        heapInit.as("heap_init"),
                        heapUsed.as("heap_used"),
                        heapCommitted.as("heap_committed"),
                        heapMax.as("heap_max"),
                        // Thread Metrics
                        threadCount.as("thread_count"),
                        peakThreadCount.as("peak_thread_count"),
                        totalStartedThreadCount.as("total_started_thread_count"),
                        // Web Server Request Metrics
                        requestCount.as("request_count"),
                        bytesReceived.as("bytes_received"),
                        bytesSent.as("bytes_sent"),
                        processingTime.as("processing_time"),
                        errorCount.as("error_count"),
                        // Thread Pool Metrics
                        maxThreads.as("max_threads"),
                        currentThreads.as("current_threads"),
                        currentThreadsBusy.as("current_threads_busy"),
                        // DBCP Metrics
                        activeConnections.as("active_connections"),
                        idleConnections.as("idle_connections"),
                        totalConnections.as("total_connections"),
                        threadsAwaitingConnection.as("threads_awaiting_connection")
                )
                .from(metricMeasurement)
                .join(agent)
                .on(metricMeasurement.AGENT_ID.equal(agent.ID))
                .groupBy(agent.AGENT_NAME,
                        agent.AGENT_BOOT_TIME,
                        agent.AGENT_VERSION,
                        timeBucket)
                .limit(limit);

        return jdbcTemplate.query(result.getSQL(), systemMeasurementRowMapper, result.getBindValues().toArray());
    }

    @Override
    public List<TransactionMetricResponse> findTxMetricsByRecentTimeAndAgentName(
            final TimeInterval interval,
            final String agentName,
            final Integer limit
    ) {
        org.traffichunter.query.jooq.tables.TransactionMeasurement transactionMeasurement = TRANSACTION_MEASUREMENT;
        org.traffichunter.query.jooq.tables.Agent agent = AGENT;

        Field<Object> timeBucket = field("time_bucket({0}, {1})",
                inline(interval.getInterval()),
                transactionMeasurement.TIME);

        Field<JSONB> traceIdGroupField = jsonbGetAttribute(transactionMeasurement.TRANSACTION_DATA, "traceId");

        SelectLimitPercentStep<Record5<Object, String, OffsetDateTime, String, JSON>> result = dsl.select(
                        timeBucket.as("bucket"),
                        agent.AGENT_NAME,
                        agent.AGENT_BOOT_TIME,
                        agent.AGENT_VERSION,
                        jsonArrayAgg(transactionMeasurement.TRANSACTION_DATA).as("transaction_datas")
                )
                .from(transactionMeasurement)
                .join(agent)
                .on(transactionMeasurement.AGENT_ID.eq(agent.ID))
                .where(agent.AGENT_NAME.eq(agentName))
                .groupBy(agent.AGENT_NAME,
                        agent.AGENT_BOOT_TIME,
                        agent.AGENT_VERSION, traceIdGroupField, timeBucket)
                .limit(limit);

        return jdbcTemplate.query(
                result.getSQL(),
                txMeasurementRowMapper,
                result.getBindValues().toArray()
        );
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
    public Slice<ServiceTransactionResponse> findServiceTransaction(final Pageable pageable) {

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
        if (hasNext) {
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

        return Optional.ofNullable(
                        jdbcTemplate.queryForObject(sql, new StatisticsMetricMaxRowMapper(), timeRange.getLatestRange()))
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

        return Optional.ofNullable(
                        jdbcTemplate.queryForObject(sql, new StatisticsMetricAvgRowMapper(), timeRange.getLatestRange()))
                .orElseThrow(() -> new IllegalArgumentException("not found avg metric"));
    }

    private Field<BigDecimal> getMetricField(final Field<JSONB> metricMeasurement, final String... path) {
        return round(
                avg(
                        DSL.cast(
                                createJsonAccessorQuery(metricMeasurement, path),
                                SQLDataType.NUMERIC
                        )
                ),
                1
        );
    }

    private Field<JSONB> createJsonAccessorQuery(final Field<JSONB> jsonb, final String... args) {

        Field<JSONB> result = jsonb;
        for (String arg : args) {
            result = jsonbGetAttribute(result, arg);
        }
        return result;
    }

}
