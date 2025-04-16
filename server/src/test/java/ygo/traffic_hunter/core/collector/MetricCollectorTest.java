package ygo.traffic_hunter.core.collector;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ygo.traffic_hunter.AbstractTestConfiguration;
import ygo.traffic_hunter.core.collector.channel.MetricChannel;

/**
 * @author yungwang-o
 * @version 1.1.0
 */
@SpringBootTest
class MetricCollectorTest extends AbstractTestConfiguration {

    @Autowired
    private Set<MetricChannel> metricChannels;

    @Test
    void 채널의_개수를_확인한다_metric_trace_log() {

        assertThat(metricChannels).hasSize(3);
    }
}