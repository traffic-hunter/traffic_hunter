package ygo.traffic_hunter.common.map;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ygo.traffic_hunter.common.map.impl.transaction.TransactionMapperImpl;
import ygo.traffic_hunter.core.dto.request.metadata.AgentMetadata;
import ygo.traffic_hunter.core.dto.request.metadata.AgentStatus;
import ygo.traffic_hunter.core.dto.request.metadata.MetadataWrapper;
import ygo.traffic_hunter.core.dto.request.transaction.TransactionInfo;
import ygo.traffic_hunter.domain.entity.TransactionMeasurement;

@SpringBootTest(classes = TransactionMapperImpl.class)
class TransactionMapperTest {

    @Autowired
    private TransactionMapper mapper;

    @Test
    void metadataWrapper를_measurement로_변환한다() {
        // given
        AgentMetadata metadata = new AgentMetadata(
                "test",
                "test",
                "test",
                Instant.now(),
                AgentStatus.RUNNING
        );

        TransactionInfo transactionInfo = new TransactionInfo(
                "test",
                Instant.ofEpochMilli(90878676),
                Instant.now(),
                1,
                "test",
                true
        );

        MetadataWrapper<TransactionInfo> metadataWrapper = new MetadataWrapper<>(metadata, transactionInfo);

        // when
        TransactionMeasurement measurement = mapper.map(metadataWrapper);

        // then
        System.out.println(measurement);
    }
}