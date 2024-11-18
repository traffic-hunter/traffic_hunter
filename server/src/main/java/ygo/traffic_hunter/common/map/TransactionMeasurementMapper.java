package ygo.traffic_hunter.common.map;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants.ComponentModel;
import ygo.traffic_hunter.dto.measurement.metric.TransactionMeasurement;
import ygo.traffic_hunter.dto.systeminfo.TransactionInfo;
import ygo.traffic_hunter.dto.systeminfo.metadata.AgentMetadata;

@Mapper(componentModel = ComponentModel.SPRING)
public interface TransactionMeasurementMapper {

    @Mapping(target = "agentName", source = "metadata.agentName")
    TransactionMeasurement map(AgentMetadata metadata, TransactionInfo transactionInfo);
}
