package ygo.traffic_hunter.common.map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.BDDMockito.given;

import java.time.Instant;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ygo.traffic_hunter.common.map.impl.transaction.TransactionMapperImpl;
import ygo.traffic_hunter.core.assembler.span.SpanTreeNode;
import ygo.traffic_hunter.core.dto.request.metadata.AgentMetadata;
import ygo.traffic_hunter.core.dto.request.metadata.AgentStatus;
import ygo.traffic_hunter.core.dto.request.metadata.MetadataWrapper;
import ygo.traffic_hunter.core.dto.response.TransactionMetricResponse;
import ygo.traffic_hunter.core.repository.AgentRepository;
import ygo.traffic_hunter.domain.entity.Agent;
import ygo.traffic_hunter.domain.entity.TransactionMeasurement;
import ygo.traffic_hunter.domain.metric.TraceInfo;
import ygo.traffic_hunter.domain.metric.TransactionData;

@ExtendWith(MockitoExtension.class)
class TransactionMapperTest {

    @InjectMocks
    private TransactionMapperImpl mapper;

    @Mock
    private AgentRepository agentRepository;

    @Test
    void metadataWrapper를_measurement로_변환한다() {
        // given
        AgentMetadata metadata = new AgentMetadata(
                "test-agent-id",
                "1.0",
                "Test Agent",
                Instant.now(),
                AgentStatus.RUNNING
        );

        TraceInfo traceInfo = new TraceInfo(
                "test-operation",
                "test-trace-id",
                "test-parent-span-id",
                "test-span-id",
                Map.of(),
                0,
                Instant.now(),
                Instant.now().plusMillis(50),
                50,
                "test",
                true
        );

        MetadataWrapper<TraceInfo> metadataWrapper = new MetadataWrapper<>(metadata, traceInfo);

        Agent mockAgent = new Agent(
                1, // ID
                "test-agent-id",
                "Test Agent",
                "1.0",
                Instant.now()
        );

        given(agentRepository.findByAgentId(metadata.agentId())).willReturn(mockAgent);

        // when
        TransactionMeasurement measurement = mapper.map(metadataWrapper);

        // then
        assertNotNull(measurement);
        assertEquals(mockAgent.id(), measurement.agentId());
        assertEquals(traceInfo.startTime(), measurement.time());
    }

    @Test
    void measurement를_response로_변환한다() {
        // given
        Agent mockAgent = new Agent(
                1, // ID
                "test-agent-id",
                "Test Agent",
                "1.0",
                Instant.now()
        );

        TransactionData transactionData = new TransactionData(
                "test-operation",
                "test-trace-id",
                "test-parent-span-id",
                "test-span-id",
                Map.of(),
                0,
                Instant.now(),
                Instant.now().plusMillis(50),
                50,
                "test",
                true
        );

        TransactionMeasurement measurement = new TransactionMeasurement(
                transactionData.startTime(),
                mockAgent.id(),
                transactionData
        );

        given(agentRepository.findById(measurement.agentId())).willReturn(mockAgent);

        // when
        TransactionMetricResponse response = mapper.map(measurement);

        // then
        assertNotNull(response);
        assertEquals(mockAgent.agentName(), response.agentName());
        assertEquals(mockAgent.agentBootTime(), response.agentBootTime());
        assertEquals(mockAgent.agentVersion(), response.agentVersion());
        assertEquals(SpanTreeNode.NO_OP, response.spanTreeNode());
    }
}