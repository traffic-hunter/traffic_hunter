package ygo.traffic_hunter.persistence.impl;

import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ygo.traffic_hunter.AbstractTestConfiguration;
import ygo.traffic_hunter.domain.entity.Agent;

@SpringBootTest
class TimeSeriesRepositoryTest extends AbstractTestConfiguration {

    @Autowired
    private TimeSeriesRepository timeSeriesRepository;

    @AfterEach
    void init() {
        timeSeriesRepository.clear();
    }

    @Test
    void 에이전트가_DB에_저장되는지_확인한다() {
        // given

        String agentId = UUID.randomUUID().toString();
        String agentName = "test";
        String agentVersion = "1.0.0";
        Instant agentBootTime = Instant.now();

        // when
        Agent agent = Agent.create(agentId, agentName, agentVersion, agentBootTime);

        // then
        timeSeriesRepository.save(agent);
    }
}