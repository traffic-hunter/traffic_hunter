package ygo.traffic_hunter.core.channel.collector.handler.systeminfo;

import lombok.Builder;
import ygo.traffic_hunter.common.map.DataToMeasurementMapper;
import ygo.traffic_hunter.core.channel.collector.handler.MetricHandler;
import ygo.traffic_hunter.core.channel.collector.processor.MetricProcessor;
import ygo.traffic_hunter.core.channel.collector.validator.MetricValidator;
import ygo.traffic_hunter.core.repository.MetricRepository;
import ygo.traffic_hunter.dto.systeminfo.SystemInfo;
import ygo.traffic_hunter.dto.systeminfo.metadata.MetadataWrapper;

public class SysteminfoMetricHandler implements MetricHandler {

    private final MetricProcessor<SystemInfo> processor;

    private final DataToMeasurementMapper mapper;

    @Builder
    public SysteminfoMetricHandler(final MetricProcessor<SystemInfo> processor,
                                   final DataToMeasurementMapper mapper) {

        this.processor = processor;
        this.mapper = mapper;
    }

    @Override
    public void handle(final byte[] payload, final MetricValidator validator, final MetricRepository repository) {

        MetadataWrapper<SystemInfo> object = processor.process(payload);

        if(validator.validate(object)) {
            repository.save(mapper.map(object));
        }
    }
}
