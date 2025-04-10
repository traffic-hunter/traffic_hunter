package ygo.traffic_hunter.domain.entity.alarm;

import org.junit.jupiter.api.Test;
import ygo.traffic_hunter.core.dto.request.systeminfo.cpu.CpuStatusInfo;
import ygo.traffic_hunter.core.dto.request.systeminfo.dbcp.HikariDbcpInfo;
import ygo.traffic_hunter.core.dto.request.systeminfo.memory.MemoryStatusInfo;
import ygo.traffic_hunter.core.dto.request.systeminfo.thread.ThreadStatusInfo;
import ygo.traffic_hunter.core.dto.request.systeminfo.web.tomcat.TomcatWebServerInfo;
import ygo.traffic_hunter.core.dto.request.systeminfo.web.tomcat.request.TomcatRequestInfo;
import ygo.traffic_hunter.core.dto.request.systeminfo.web.tomcat.thread.TomcatThreadPoolInfo;
import ygo.traffic_hunter.core.dto.response.alarm.ThresholdResponse;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * @author JuSeong
 * @version 1.1.0
 */
class ThresholdTest {


    @Test
    void test() {
        // given
        MemoryStatusInfo memoryStatusInfo = new MemoryStatusInfo(
                new MemoryStatusInfo.MemoryUsage(1000L, 500L, 800L, 1024L),
                new MemoryStatusInfo.MemoryUsage(500L, 200L, 400L, 512L)
        );
        ThreadStatusInfo threadStatusInfo = new ThreadStatusInfo(10, 15, 100L);
        CpuStatusInfo cpuStatusInfo = new CpuStatusInfo(0.5, 0.3, 4);

        TomcatWebServerInfo tomcatWebServerInfo = new TomcatWebServerInfo(
                new TomcatThreadPoolInfo(1, 1, 1),
                new TomcatRequestInfo(1, 1, 1, 1, 1)
        );
        HikariDbcpInfo hikariDbcpInfo = new HikariDbcpInfo(1, 1, 1, 1);

        Threshold.Calculator calculator = Threshold.Calculator.builder()
                .memoryStatusInfo(memoryStatusInfo)
                .cpuStatusInfo(cpuStatusInfo)
                .threadStatusInfo(threadStatusInfo)
                .tomcatWebServerInfo(tomcatWebServerInfo)
                .hikariDbcpInfo(hikariDbcpInfo)
                .build();

        ThresholdResponse thresholdResponse = new ThresholdResponse(
                80,
                80
                , 80
                , 80
                , 80
                , 80
        );
        // 예상 값 미리 계산
        long expectedMemory = (long) (1024L * 0.8); // 1024L * 80 / 100
        long expectedThread = (long) (100L * 0.8); // 100L * 80 / 100
        int expectedWebThread = (int) (1 * 0.8); // 1 * 80 / 100
        int expectedDbcp = (int) (1 * 0.8); // 1 * 80 / 100

        // when
        Threshold.CalculatedThreshold calculate = calculator.calculate(thresholdResponse);

        // then
        assertThat(calculate.calculateCpu()).isEqualTo(80); // cpuThreshold = 80
        assertThat(calculate.calculateMemory()).isEqualTo(expectedMemory);
        assertThat(calculate.calculateThread()).isEqualTo(expectedThread);
        assertThat(calculate.calculateWebRequest()).isEqualTo(80); // webRequestThreshold = 80
        assertThat(calculate.calculateWebThread()).isEqualTo(expectedWebThread);
        assertThat(calculate.calculateDbcp()).isEqualTo(expectedDbcp);

    }

}