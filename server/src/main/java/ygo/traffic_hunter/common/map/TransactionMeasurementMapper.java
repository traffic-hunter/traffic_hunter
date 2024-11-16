package ygo.traffic_hunter.common.map;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;
import ygo.traffic_hunter.domain.measurement.metric.TransactionMeasurement;
import ygo.traffic_hunter.presentation.response.systeminfo.TransactionInfo;

@Mapper(componentModel = ComponentModel.SPRING)
public interface TransactionMeasurementMapper {

    TransactionMeasurement map(TransactionInfo transactionInfo);
}
