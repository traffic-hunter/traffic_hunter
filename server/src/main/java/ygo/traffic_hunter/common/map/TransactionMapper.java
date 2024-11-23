package ygo.traffic_hunter.common.map;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants.ComponentModel;
import org.mapstruct.Named;
import ygo.traffic_hunter.core.dto.request.metadata.MetadataWrapper;
import ygo.traffic_hunter.core.dto.request.transaction.TransactionInfo;
import ygo.traffic_hunter.core.dto.response.TransactionMetricResponse;
import ygo.traffic_hunter.domain.entity.TransactionMeasurement;
import ygo.traffic_hunter.domain.metric.TransactionData;

@Mapper(componentModel = ComponentModel.SPRING)
public interface TransactionMapper {

    @Mapping(target = "time", source = "data.startTime")
    @Mapping(target = "agentBootTime", source = "metadata.startTime")
    @Mapping(target = "agentVersion", source = "metadata.agentVersion")
    @Mapping(target = "agentName", source = "metadata.agentName")
    @Mapping(target = "agentId", source = "metadata.agentId")
    @Mapping(target = "transactionData", source = "data", qualifiedByName = "toTransactionData")
    TransactionMeasurement map(MetadataWrapper<TransactionInfo> wrapper);

    TransactionMetricResponse map(TransactionMeasurement measurement);

    @Named("toTransactionData")
    default TransactionData mapTransactionData(TransactionInfo transactionInfo) {
        return new TransactionData(
                transactionInfo.txName(),
                transactionInfo.startTime(),
                transactionInfo.endTime(),
                transactionInfo.duration(),
                transactionInfo.errorMessage(),
                transactionInfo.isSuccess()
        );
    }
}
