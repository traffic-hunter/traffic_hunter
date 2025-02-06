package ygo.traffic_hunter.core.service;

import static org.assertj.core.api.Assertions.assertThat;
import static ygo.traffic_hunter.core.statistics.StatisticsMetricTimeRange.LATEST_ONE_DAY;

import java.time.Duration;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import ygo.traffic_hunter.AbstractTestConfiguration;
import ygo.traffic_hunter.core.dto.response.statistics.metric.StatisticsMetricAvgResponse;
import ygo.traffic_hunter.core.dto.response.statistics.metric.StatisticsMetricMaxResponse;
import ygo.traffic_hunter.core.dto.response.statistics.transaction.ServiceTransactionResponse;

@SpringBootTest
class MetricStatisticsServiceTest extends AbstractTestConfiguration {

    @Autowired
    private MetricStatisticsService metricStatisticsService;

    @Test
    void 시간_범위를_통해_서비스_트랜잭션을_조회한다() {
        // given
        Instant begin = Instant.now().minus(Duration.ofDays(40));
        Instant end = Instant.now();

        PageRequest pr = PageRequest.of(0, 10);

        // when
        Slice<ServiceTransactionResponse> responses =
                metricStatisticsService.retrieveServiceTransactions(begin, end, pr);

        // then
        assertThat(responses.getNumber()).isEqualTo(0);
        assertThat(responses.getSize()).isEqualTo(10);
        System.out.println(responses.getContent());
    }

    @Test
    void 최근_가장_높은_메트릭을_조회한다() {
        // given

        // when
        StatisticsMetricMaxResponse result = metricStatisticsService.retrieveStatisticsMaxMetric(
                LATEST_ONE_DAY);

        // then
        System.out.println(result);
    }

    @Test
    void 최근_평균_메트릭을_조회한다() {
        // given

        // when
        StatisticsMetricAvgResponse result = metricStatisticsService.retrieveStatisticsAvgMetric(LATEST_ONE_DAY);

        // then
        assertThat(result).isNotNull();
        System.out.println(result);
    }
}