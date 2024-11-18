package ygo.traffic_hunter.common.map;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants.ComponentModel;
import ygo.traffic_hunter.dto.measurement.metadata.Metadata;
import ygo.traffic_hunter.dto.measurement.metric.TransactionMeasurement;
import ygo.traffic_hunter.dto.systeminfo.TransactionInfo;
import ygo.traffic_hunter.dto.systeminfo.metadata.AgentMetadata;
import ygo.traffic_hunter.dto.systeminfo.metadata.MetadataWrapper;

@Mapper(componentModel = ComponentModel.SPRING)
public interface TransactionMeasurementMapper {

    @Mapping(source = "metadata", target = "metadata")
    @Mapping(target = "txName", source = "data.txName")
    @Mapping(target = "startTime", source = "data.startTime")
    @Mapping(target = "isSuccess", source = "data.isSuccess")
    @Mapping(target = "errorMessage", source = "data.errorMessage")
    @Mapping(target = "endTime", source = "data.endTime")
    @Mapping(target = "duration", source = "data.duration")
    TransactionMeasurement map(MetadataWrapper<TransactionInfo> wrapper);

    @Mapping(target = "agentStatus", source = "agentMetadata.status")
    @Mapping(target = "agentBootTime", source = "agentMetadata.startTime")
    Metadata agentMetadataToMetadata(AgentMetadata agentMetadata);
}
