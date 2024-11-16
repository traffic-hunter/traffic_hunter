package ygo.traffic_hunter.common.map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ygo.traffic_hunter.AbstractTestConfiguration;
import ygo.traffic_hunter.domain.measurement.metric.MetricMeasurement;
import ygo.traffic_hunter.presentation.response.systeminfo.SystemInfo;
import ygo.traffic_hunter.presentation.response.systeminfo.cpu.CpuStatusInfo;
import ygo.traffic_hunter.presentation.response.systeminfo.gc.GarbageCollectionStatusInfo;
import ygo.traffic_hunter.presentation.response.systeminfo.gc.collections.GarbageCollectionTime;
import ygo.traffic_hunter.presentation.response.systeminfo.memory.MemoryStatusInfo;
import ygo.traffic_hunter.presentation.response.systeminfo.runtime.RuntimeStatusInfo;
import ygo.traffic_hunter.presentation.response.systeminfo.thread.ThreadStatusInfo;

//@SpringBootTest(classes = DataToMeasurementMapperImpl.class)
class DataToMeasurementMapperTest extends AbstractTestConfiguration {

//    @Autowired
//    private DataToMeasurementMapper mapper;
//
//    @Test
//    void DTO_TO_MEASUREMENT_매핑을_성공한다() {
//        // given
//        Instant now = Instant.now();
//        SystemInfo systemInfo = new SystemInfo(
//                now,
//                "TestJVM",
//                new MemoryStatusInfo(
//                        new MemoryStatusInfo.MemoryUsage(1000L, 500L, 800L, 1024L),
//                        new MemoryStatusInfo.MemoryUsage(500L, 200L, 400L, 512L)
//                ),
//                new ThreadStatusInfo(10, 15, 100L),
//                new CpuStatusInfo(0.5, 0.3, 4),
//                new GarbageCollectionStatusInfo(Collections.singletonList(
//                        new GarbageCollectionTime(5L, 100L)
//                )),
//                new RuntimeStatusInfo(1000L, 5000L, "TestVM", "1.0")
//        );
//
//        // when
//        MetricMeasurement result = mapper.systemInfoToMetricMeasurement(systemInfo);
//
//        // Assert
//        assertThat(result).isNotNull();
//        assertThat(result.time()).isEqualTo(now);
//        assertThat(result.clientIp()).isNull(); // Since it's ignored in the mapper
//
//        // CPU Metric assertions
//        assertThat(result.cpuMetric().systemCpuLoad()).isEqualTo(0.5);
//        assertThat(result.cpuMetric().processCpuLoad()).isEqualTo(0.3);
//        assertThat(result.cpuMetric().availableProcessors()).isEqualTo(4);
//
//        // Memory Metric assertions
//        assertThat(result.memoryMetric().heapMemoryUsage().init()).isEqualTo(1000L);
//        assertThat(result.memoryMetric().heapMemoryUsage().used()).isEqualTo(500L);
//        assertThat(result.memoryMetric().heapMemoryUsage().committed()).isEqualTo(800L);
//        assertThat(result.memoryMetric().heapMemoryUsage().max()).isEqualTo(1024L);
//
//        assertThat(result.memoryMetric().nonHeapMemoryUsage().init()).isEqualTo(500L);
//        assertThat(result.memoryMetric().nonHeapMemoryUsage().used()).isEqualTo(200L);
//        assertThat(result.memoryMetric().nonHeapMemoryUsage().committed()).isEqualTo(400L);
//        assertThat(result.memoryMetric().nonHeapMemoryUsage().max()).isEqualTo(512L);
//
//        // Thread Metric assertions
//        assertThat(result.threadMetric().threadCount()).isEqualTo(10);
//        assertThat(result.threadMetric().getPeekThreadCount()).isEqualTo(15);
//        assertThat(result.threadMetric().getTotalStartThreadCount()).isEqualTo(100L);
//
//        // GC Metric assertions
//        assertThat(result.gcMetric().gcMetricCollectionTimes()).hasSize(1);
//        assertThat(result.gcMetric().gcMetricCollectionTimes().getFirst().getCollectionCount()).isEqualTo(5L);
//        assertThat(result.gcMetric().gcMetricCollectionTimes().getFirst().getCollectionTime()).isEqualTo(100L);
//
//        // Runtime Metric assertions
//        assertThat(result.runtimeMetric().getStartTime()).isEqualTo(1000L);
//        assertThat(result.runtimeMetric().getUpTime()).isEqualTo(5000L);
//        assertThat(result.runtimeMetric().getVmName()).isEqualTo("TestVM");
//        assertThat(result.runtimeMetric().getVmVersion()).isEqualTo("1.0");
//    }
}