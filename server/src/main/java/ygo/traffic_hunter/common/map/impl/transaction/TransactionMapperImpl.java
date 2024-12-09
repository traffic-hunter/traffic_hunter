package ygo.traffic_hunter.common.map.impl.transaction;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ygo.traffic_hunter.common.map.TransactionMapper;
import ygo.traffic_hunter.core.dto.request.metadata.AgentMetadata;
import ygo.traffic_hunter.core.dto.request.metadata.MetadataWrapper;
import ygo.traffic_hunter.core.dto.request.transaction.TransactionInfo;
import ygo.traffic_hunter.core.dto.response.TransactionMetricResponse;
import ygo.traffic_hunter.core.repository.AgentRepository;
import ygo.traffic_hunter.domain.entity.Agent;
import ygo.traffic_hunter.domain.entity.TransactionMeasurement;
import ygo.traffic_hunter.domain.metric.TransactionData;

/**
 * @author yungwang-o
 * @version 1.0.0
 */
@Component
@RequiredArgsConstructor
public class TransactionMapperImpl implements TransactionMapper {

    private final AgentRepository agentRepository;

    @Override
    public TransactionMeasurement map(final MetadataWrapper<TransactionInfo> wrapper) {

        AgentMetadata metadata = wrapper.metadata();

        TransactionInfo data = wrapper.data();

        Agent agent = agentRepository.findByAgentId(metadata.agentId());

        return new TransactionMeasurement(
                data.startTime(),
                agent.id(),
                getTransactionData(data)
        );
    }

    @Override
    public TransactionMetricResponse map(final TransactionMeasurement measurement) {

        Agent agent = agentRepository.findById(measurement.agentId());

        return new TransactionMetricResponse(
                agent.agentName(),
                agent.agentBootTime(),
                agent.agentVersion(),
                measurement.transactionData()
        );
    }

    private TransactionData getTransactionData(final TransactionInfo data) {
        return new TransactionData(
                data.txName(),
                data.startTime(),
                data.endTime(),
                data.duration(),
                data.errorMessage(),
                data.isSuccess()
        );
    }
}
