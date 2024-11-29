package ygo.traffic_hunter.common.map.impl.transaction;

import org.springframework.stereotype.Component;
import ygo.traffic_hunter.common.map.TransactionMapper;
import ygo.traffic_hunter.core.dto.request.metadata.AgentMetadata;
import ygo.traffic_hunter.core.dto.request.metadata.MetadataWrapper;
import ygo.traffic_hunter.core.dto.request.transaction.TransactionInfo;
import ygo.traffic_hunter.core.dto.response.TransactionMetricResponse;
import ygo.traffic_hunter.domain.entity.TransactionMeasurement;
import ygo.traffic_hunter.domain.metric.TransactionData;

@Component
public class TransactionMapperImpl implements TransactionMapper {

    @Override
    public TransactionMeasurement map(final MetadataWrapper<TransactionInfo> wrapper) {

        AgentMetadata metadata = wrapper.metadata();

        TransactionInfo data = wrapper.data();

        return new TransactionMeasurement(
                data.startTime(),
                metadata.agentId(),
                metadata.agentName(),
                metadata.agentVersion(),
                metadata.startTime(),
                getTransactionData(data)
        );
    }

    @Override
    public TransactionMetricResponse map(final TransactionMeasurement measurement) {
        return new TransactionMetricResponse(
                measurement.agentName(),
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
